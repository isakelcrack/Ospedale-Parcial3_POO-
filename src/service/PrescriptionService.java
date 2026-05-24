package service;

import dto.PrescriptionRequest;
import dto.AppointmentTableDto;
import java.util.Optional;
import model.Appointment;
import model.AppointmentStatus;
import model.Doctor;
import model.Prescription;
import model.User;
import repository.AppointmentRepository;
import repository.UserRepository;
import response.Response;

public class PrescriptionService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public PrescriptionService(AppointmentRepository appointmentRepository, UserRepository userRepository,
            ValidationService validationService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public Response<AppointmentTableDto> prescribe(PrescriptionRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de la prescripcion son obligatorios.");
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
            return Response.badRequest("No se puede prescribir en citas completadas o canceladas.");
        }
        if (appointment.get().getStatus() != AppointmentStatus.PENDING) {
            return Response.badRequest("Solo se puede prescribir en citas PENDING.");
        }

        double dose;
        try {
            dose = Double.parseDouble(request.getDose().trim());
        } catch (RuntimeException ex) {
            return Response.badRequest("La dosis debe ser numerica.");
        }
        int treatmentDuration;
        try {
            treatmentDuration = Integer.parseInt(request.getTreatmentDuration().trim());
        } catch (RuntimeException ex) {
            return Response.badRequest("La duracion del tratamiento debe ser numerica.");
        }
        int frequency;
        try {
            frequency = Integer.parseInt(request.getFrequency().trim());
        } catch (RuntimeException ex) {
            return Response.badRequest("La frecuencia debe ser numerica.");
        }

        Prescription prescription = new Prescription(
                appointment.get(),
                request.getMedicationName(),
                dose,
                request.getAdministrationRoute(),
                treatmentDuration,
                request.getAdditionalInstructions(),
                frequency
        );
        appointmentRepository.save(appointment.get());
        return Response.created("Medicamento prescrito exitosamente.", toAppointmentDto(prescription.getAppointment()));
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

    private Optional<Doctor> findDoctor(String id) {
        Optional<User> user = userRepository.findById(Long.parseLong(id.trim()));
        if (user.isPresent() && user.get() instanceof Doctor) {
            return Optional.of((Doctor) user.get());
        }
        return Optional.empty();
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

    private AppointmentTableDto toAppointmentDto(Appointment appointment) {
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
