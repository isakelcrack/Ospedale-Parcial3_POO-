package service;

import dto.PrescriptionRequest;
import dto.AppointmentTableDto;
import java.util.Optional;
import model.Appointment;
import model.AppointmentStatus;
import model.Prescription;
import repository.AppointmentRepository;
import response.Response;

public class PrescriptionService {

    private final AppointmentRepository appointmentRepository;
    private final ValidationService validationService;

    public PrescriptionService(AppointmentRepository appointmentRepository, ValidationService validationService) {
        this.appointmentRepository = appointmentRepository;
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
