package service;

import dto.HospitalizationRequest;
import dto.HospitalizationTableDto;
import java.time.LocalDate;
import java.util.Optional;
import model.Appointment;
import model.AppointmentStatus;
import model.Doctor;
import model.Hospitalization;
import model.HospitalizationStatus;
import model.Patient;
import model.RoomType;
import model.User;
import repository.AppointmentRepository;
import repository.HospitalizationRepository;
import repository.UserRepository;
import response.Response;

public class HospitalizationService {

    private final HospitalizationRepository hospitalizationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public HospitalizationService(HospitalizationRepository hospitalizationRepository,
            AppointmentRepository appointmentRepository, UserRepository userRepository,
            ValidationService validationService) {
        this.hospitalizationRepository = hospitalizationRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public Response<HospitalizationTableDto> requestHospitalization(HospitalizationRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de hospitalizacion son obligatorios.");
        }

        Response<Void> validation = validateBasicRequest(request);
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Patient> patient = findPatient(request.getPatientId());
        if (patient.isEmpty()) {
            return Response.notFound("Paciente no encontrado.");
        }
        Optional<Doctor> doctor = findDoctor(request.getDoctorId());
        if (doctor.isEmpty()) {
            return Response.notFound("Doctor no encontrado.");
        }

        RoomType roomType = parseRoomType(request.getRoomType());
        String hospitalizationId = buildHospitalizationId(patient.get().getId());
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                patient.get(),
                doctor.get(),
                LocalDate.parse(request.getDate().trim()),
                request.getReason(),
                roomType,
                request.getObservations(),
                HospitalizationStatus.REQUESTED
        );
        hospitalizationRepository.save(hospitalization);
        return Response.created("Hospitalizacion solicitada exitosamente.", toTableDto(hospitalization));
    }

    public Response<HospitalizationTableDto> approveHospitalization(String hospitalizationId) {
        Optional<Hospitalization> hospitalization = hospitalizationRepository.findById(hospitalizationId);
        if (hospitalization.isEmpty()) {
            return Response.notFound("Hospitalizacion no encontrada.");
        }
        if (hospitalization.get().getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.badRequest("Solo se pueden aprobar hospitalizaciones en estado REQUESTED.");
        }
        hospitalization.get().setStatus(HospitalizationStatus.ONGOING);
        hospitalizationRepository.save(hospitalization.get());
        return Response.ok("Hospitalizacion aprobada exitosamente.", toTableDto(hospitalization.get()));
    }

    public Response<HospitalizationTableDto> cancelHospitalization(String hospitalizationId) {
        Optional<Hospitalization> hospitalization = hospitalizationRepository.findById(hospitalizationId);
        if (hospitalization.isEmpty()) {
            return Response.notFound("Hospitalizacion no encontrada.");
        }
        if (hospitalization.get().getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.badRequest("Solo se pueden cancelar hospitalizaciones en estado REQUESTED.");
        }
        hospitalization.get().setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.save(hospitalization.get());
        return Response.ok("Hospitalizacion cancelada exitosamente.", toTableDto(hospitalization.get()));
    }

    public Response<HospitalizationTableDto> denyHospitalization(String hospitalizationId) {
        Optional<Hospitalization> hospitalization = hospitalizationRepository.findById(hospitalizationId);
        if (hospitalization.isEmpty()) {
            return Response.notFound("Hospitalizacion no encontrada.");
        }
        if (hospitalization.get().getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.badRequest("Solo se pueden denegar hospitalizaciones en estado REQUESTED.");
        }
        hospitalization.get().setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.save(hospitalization.get());
        return Response.ok("Hospitalizacion denegada exitosamente.", toTableDto(hospitalization.get()));
    }

    public Response<HospitalizationTableDto> createHospitalizationFromAppointment(HospitalizationRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de hospitalizacion son obligatorios.");
        }
        if (request.getAppointmentId() == null || request.getAppointmentId().trim().isEmpty()) {
            return Response.badRequest("El id de la cita es obligatorio.");
        }
        Response<Void> validation = validationService.validateDate(request.getDate());
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }
        validation = validationService.validateUserId(request.getDoctorId());
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointment = appointmentRepository.findById(request.getAppointmentId().trim());
        if (appointment.isEmpty()) {
            return Response.notFound("Cita no encontrada.");
        }
        Optional<Doctor> requestedDoctor = findDoctor(request.getDoctorId());
        if (requestedDoctor.isEmpty()) {
            return Response.notFound("Doctor no encontrado.");
        }
        if (appointment.get().getDoctor().getId() != requestedDoctor.get().getId()) {
            return Response.forbidden("La cita no pertenece al doctor indicado.");
        }
        if (appointment.get().getStatus() != AppointmentStatus.PENDING) {
            return Response.badRequest("Solo se puede hospitalizar desde una cita PENDING.");
        }

        Patient patient = appointment.get().getPatient();
        Doctor doctor = appointment.get().getDoctor();
        RoomType roomType;
        try {
            roomType = parseRoomType(request.getRoomType());
        } catch (IllegalArgumentException ex) {
            return Response.badRequest("El tipo de habitacion no es valido.");
        }
        String hospitalizationId = buildHospitalizationId(patient.getId());

        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                patient,
                doctor,
                LocalDate.parse(request.getDate().trim()),
                request.getReason(),
                roomType,
                request.getObservations(),
                HospitalizationStatus.ONGOING
        );
        appointment.get().setStatus(AppointmentStatus.COMPLETED);
        hospitalizationRepository.save(hospitalization);
        appointmentRepository.save(appointment.get());
        return Response.created("Hospitalizacion creada desde cita exitosamente.", toTableDto(hospitalization));
    }

    private Response<Void> validateBasicRequest(HospitalizationRequest request) {
        Response<Void> validation = validationService.validateUserId(request.getPatientId());
        if (!validation.isSuccess()) {
            return validation;
        }
        validation = validationService.validateUserId(request.getDoctorId());
        if (!validation.isSuccess()) {
            return validation;
        }
        validation = validationService.validateDate(request.getDate());
        if (!validation.isSuccess()) {
            return validation;
        }
        try {
            parseRoomType(request.getRoomType());
        } catch (IllegalArgumentException ex) {
            return Response.badRequest("El tipo de habitacion no es valido.");
        }
        return Response.ok("Datos validos.", null);
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

    private RoomType parseRoomType(String roomType) {
        return RoomType.fromText(roomType);
    }

    private String buildHospitalizationId(long patientId) {
        int count = hospitalizationRepository.countByPatientId(patientId);
        return String.format("H-%d-%04d", patientId, count);
    }

    private HospitalizationTableDto toTableDto(Hospitalization hospitalization) {
        return new HospitalizationTableDto(
                hospitalization.getId(),
                hospitalization.getPatient().getId(),
                hospitalization.getPatient().getFullName(),
                hospitalization.getDoctor().getId(),
                hospitalization.getDoctor().getFullName(),
                hospitalization.getDate().toString(),
                hospitalization.getReason(),
                hospitalization.getRoomType().name(),
                hospitalization.getObservations(),
                hospitalization.getStatus().name()
        );
    }
}
