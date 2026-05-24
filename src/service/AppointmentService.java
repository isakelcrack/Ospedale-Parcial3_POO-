package service;

import dto.AppointmentRequest;
import dto.AppointmentTableDto;
import dto.AppointmentUpdateRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import model.Appointment;
import model.AppointmentStatus;
import model.Doctor;
import model.Patient;
import model.Specialty;
import model.User;
import repository.AppointmentRepository;
import repository.UserRepository;
import response.Response;

public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository,
            ValidationService validationService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public Response<AppointmentTableDto> requestAppointment(AppointmentRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de la cita son obligatorios.");
        }

        Response<Void> validation = validationService.validateUserId(request.getPatientId());
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }
        validation = validationService.validateDate(request.getDate());
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }
        validation = validationService.validateQuarterHour(request.getTime());
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Patient> patient = findPatient(request.getPatientId());
        if (patient.isEmpty()) {
            return Response.notFound("Paciente no encontrado.");
        }

        LocalDateTime datetime = LocalDateTime.of(
                LocalDate.parse(request.getDate().trim()),
                LocalTime.parse(request.getTime().trim())
        );

        Doctor doctor;
        Specialty specialty;
        if (request.getDoctorId() != null && !request.getDoctorId().trim().isEmpty()) {
            validation = validationService.validateUserId(request.getDoctorId());
            if (!validation.isSuccess()) {
                return Response.badRequest(validation.getMessage());
            }
            Optional<Doctor> selectedDoctor = findDoctor(request.getDoctorId());
            if (selectedDoctor.isEmpty()) {
                return Response.notFound("Doctor no encontrado.");
            }
            doctor = selectedDoctor.get();
            specialty = doctor.getSpecialty();
            if (!isDoctorAvailable(doctor, datetime, null)) {
                return Response.badRequest("El doctor no tiene disponibilidad en el horario solicitado.");
            }
        } else {
            try {
                specialty = Specialty.fromText(request.getSpecialty());
            } catch (IllegalArgumentException ex) {
                return Response.badRequest("La especialidad no es valida.");
            }
            Optional<Doctor> availableDoctor = findAvailableDoctor(specialty, datetime);
            if (availableDoctor.isEmpty()) {
                return Response.badRequest("No hay doctores disponibles para la especialidad y horario solicitados.");
            }
            doctor = availableDoctor.get();
        }

        String appointmentId = buildAppointmentId(patient.get().getId());
        Appointment appointment = new Appointment(
                appointmentId,
                patient.get(),
                doctor,
                specialty,
                datetime,
                request.getReason(),
                request.isInPerson()
        );
        appointmentRepository.save(appointment);
        return Response.created("Cita solicitada exitosamente.", toTableDto(appointment));
    }

    public Response<AppointmentTableDto> acceptAppointment(String appointmentId) {
        return acceptAppointment(appointmentId, null);
    }

    public Response<AppointmentTableDto> acceptAppointment(String appointmentId, String doctorId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            return Response.notFound("Cita no encontrada.");
        }
        Response<Void> doctorValidation = validateAppointmentDoctor(doctorId, appointment.get());
        if (!doctorValidation.isSuccess()) {
            return appointmentError(doctorValidation);
        }
        if (appointment.get().getStatus() != AppointmentStatus.REQUESTED) {
            return Response.badRequest("Solo se pueden aceptar citas en estado REQUESTED.");
        }
        appointment.get().setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment.get());
        return Response.ok("Cita aceptada exitosamente.", toTableDto(appointment.get()));
    }

    public Response<AppointmentTableDto> cancelAppointment(String appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            return Response.notFound("Cita no encontrada.");
        }
        if (appointment.get().getStatus() == AppointmentStatus.COMPLETED) {
            return Response.badRequest("Una cita completada no se puede cancelar.");
        }
        appointment.get().setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment.get());
        return Response.ok("Cita cancelada exitosamente.", toTableDto(appointment.get()));
    }

    public Response<AppointmentTableDto> rescheduleAppointment(AppointmentUpdateRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de reagendamiento son obligatorios.");
        }
        Response<Void> validation = validationService.validateQuarterHour(request.getTime());
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointment = appointmentRepository.findById(request.getAppointmentId());
        if (appointment.isEmpty()) {
            return Response.notFound("Cita no encontrada.");
        }
        Response<Void> doctorValidation = validateAppointmentDoctor(request.getDoctorId(), appointment.get());
        if (!doctorValidation.isSuccess()) {
            return appointmentError(doctorValidation);
        }
        if (appointment.get().getStatus() == AppointmentStatus.COMPLETED
                || appointment.get().getStatus() == AppointmentStatus.CANCELED) {
            return Response.badRequest("No se puede reagendar una cita completada o cancelada.");
        }

        LocalDateTime newDatetime = LocalDateTime.of(
                appointment.get().getDatetime().toLocalDate(),
                LocalTime.parse(request.getTime().trim())
        );
        if (!isDoctorAvailable(appointment.get().getDoctor(), newDatetime, appointment.get().getId())) {
            return Response.badRequest("El doctor no tiene disponibilidad en la nueva hora.");
        }

        appointment.get().setDatetime(newDatetime);
        if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
            appointment.get().setReason(appendReason(appointment.get().getReason(), request.getReason().trim()));
        }
        appointmentRepository.save(appointment.get());
        return Response.ok("Cita reagendada exitosamente.", toTableDto(appointment.get()));
    }

    public Response<AppointmentTableDto> completeAppointment(AppointmentUpdateRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de finalizacion son obligatorios.");
        }
        Optional<Appointment> appointment = appointmentRepository.findById(request.getAppointmentId());
        if (appointment.isEmpty()) {
            return Response.notFound("Cita no encontrada.");
        }
        Response<Void> doctorValidation = validateAppointmentDoctor(request.getDoctorId(), appointment.get());
        if (!doctorValidation.isSuccess()) {
            return appointmentError(doctorValidation);
        }
        if (appointment.get().getStatus() != AppointmentStatus.PENDING) {
            return Response.badRequest("Solo se pueden completar citas en estado PENDING.");
        }

        appointment.get().setDiagnosis(request.getDiagnosis());
        appointment.get().setObservations(request.getObservations());
        appointment.get().setRecommendedTreatment(request.getRecommendedTreatment());
        appointment.get().setFollowUp(request.getFollowUp());
        appointment.get().setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment.get());
        return Response.ok("Cita completada exitosamente.", toTableDto(appointment.get()));
    }

    public Response<List<AppointmentTableDto>> getPatientAppointments(String patientId) {
        Response<Void> validation = validationService.validateUserId(patientId);
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }
        Optional<Patient> patient = findPatient(patientId);
        if (patient.isEmpty()) {
            return Response.notFound("Paciente no encontrado.");
        }

        List<Appointment> appointments = new ArrayList<>();
        for (Appointment appointment : appointmentRepository.findAll()) {
            if (appointment.getPatient().getId() == patient.get().getId()) {
                appointments.add(appointment);
            }
        }
        return Response.ok("Citas del paciente encontradas.", toSortedAppointmentDtos(appointments));
    }

    public Response<List<AppointmentTableDto>> getDoctorAppointments(String doctorId, boolean pendingOnly) {
        Response<Void> validation = validationService.validateUserId(doctorId);
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }
        Optional<Doctor> doctor = findDoctor(doctorId);
        if (doctor.isEmpty()) {
            return Response.notFound("Doctor no encontrado.");
        }

        List<Appointment> appointments = new ArrayList<>();
        for (Appointment appointment : appointmentRepository.findAll()) {
            boolean belongsToDoctor = appointment.getDoctor().getId() == doctor.get().getId();
            boolean hasRequestedStatus = !pendingOnly || appointment.getStatus() == AppointmentStatus.PENDING;
            if (belongsToDoctor && hasRequestedStatus) {
                appointments.add(appointment);
            }
        }
        return Response.ok("Citas del doctor encontradas.", toSortedAppointmentDtos(appointments));
    }

    private Optional<Patient> findPatient(String id) {
        Optional<User> user = userRepository.findById(Long.parseLong(id.trim()));
        if (user.isPresent() && user.get() instanceof Patient) {
            return Optional.of((Patient) user.get());
        }
        return Optional.empty();
    }

    private Optional<Doctor> findDoctor(String id) {
        Optional<User> user = userRepository.findById(Long.parseLong(id.trim()));
        if (user.isPresent() && user.get() instanceof Doctor) {
            return Optional.of((Doctor) user.get());
        }
        return Optional.empty();
    }

    private Response<Void> validateAppointmentDoctor(String doctorId, Appointment appointment) {
        Response<Void> validation = validationService.validateUserId(doctorId);
        if (!validation.isSuccess()) {
            return validation;
        }
        Optional<Doctor> doctor = findDoctor(doctorId);
        if (doctor.isEmpty()) {
            return Response.notFound("Doctor no encontrado.");
        }
        if (appointment.getDoctor().getId() != doctor.get().getId()) {
            return Response.forbidden("La cita no pertenece al doctor indicado.");
        }
        return Response.ok("Doctor valido para la cita.", null);
    }

    private Response<AppointmentTableDto> appointmentError(Response<Void> response) {
        if (response.getStatusCode() == 403) {
            return Response.forbidden(response.getMessage());
        }
        if (response.getStatusCode() == 404) {
            return Response.notFound(response.getMessage());
        }
        return Response.badRequest(response.getMessage());
    }

    private Optional<Doctor> findAvailableDoctor(Specialty specialty, LocalDateTime datetime) {
        for (Doctor doctor : userRepository.findDoctors()) {
            if (doctor.getSpecialty() == specialty && isDoctorAvailable(doctor, datetime, null)) {
                return Optional.of(doctor);
            }
        }
        return Optional.empty();
    }

    private boolean isDoctorAvailable(Doctor doctor, LocalDateTime datetime, String ignoredAppointmentId) {
        for (Appointment appointment : appointmentRepository.findAll()) {
            boolean sameAppointment = ignoredAppointmentId != null && ignoredAppointmentId.equals(appointment.getId());
            boolean activeStatus = appointment.getStatus() != AppointmentStatus.CANCELED;
            boolean sameDoctor = appointment.getDoctor().getId() == doctor.getId();
            boolean sameTime = appointment.getDatetime().equals(datetime);
            if (!sameAppointment && activeStatus && sameDoctor && sameTime) {
                return false;
            }
        }
        return true;
    }

    private String buildAppointmentId(long patientId) {
        int count = appointmentRepository.countByPatientId(patientId);
        return String.format("A-%d-%04d", patientId, count);
    }

    private String appendReason(String currentReason, String newReason) {
        if (currentReason == null || currentReason.isBlank()) {
            return newReason;
        }
        return currentReason + " | Reprogramacion: " + newReason;
    }

    private List<AppointmentTableDto> toSortedAppointmentDtos(List<Appointment> appointments) {
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<AppointmentTableDto> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            result.add(toTableDto(appointment));
        }
        return result;
    }

    private AppointmentTableDto toTableDto(Appointment appointment) {
        return new AppointmentTableDto(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFullName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFullName(),
                appointment.getSpecialty().displayName(),
                appointment.getDatetime().toLocalDate().toString(),
                appointment.getDatetime().toLocalTime().toString(),
                appointment.getReason(),
                appointment.isInPerson(),
                appointment.getStatus().name(),
                appointment.getDiagnosis(),
                appointment.getObservations(),
                appointment.getRecommendedTreatment(),
                appointment.getFollowUp()
        );
    }
}
