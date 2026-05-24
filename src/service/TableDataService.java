package service;

import dto.AppointmentTableDto;
import dto.DoctorOptionDto;
import dto.HospitalizationTableDto;
import dto.PatientOptionDto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.Appointment;
import model.AppointmentStatus;
import model.Doctor;
import model.Hospitalization;
import model.Patient;
import repository.AppointmentRepository;
import repository.HospitalizationRepository;
import repository.UserRepository;
import response.Response;

public class TableDataService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;

    public TableDataService(UserRepository userRepository, AppointmentRepository appointmentRepository,
            HospitalizationRepository hospitalizationRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    public Response<List<DoctorOptionDto>> getDoctorOptions() {
        List<DoctorOptionDto> result = new ArrayList<>();
        for (Doctor doctor : userRepository.findDoctors()) {
            result.add(new DoctorOptionDto(
                    doctor.getId(),
                    doctor.getFullName(),
                    doctor.getSpecialty().displayName(),
                    doctor.getAssignedOffice()
            ));
        }
        return Response.ok("Doctores encontrados.", result);
    }

    public Response<List<PatientOptionDto>> getPatientOptions() {
        List<PatientOptionDto> result = new ArrayList<>();
        for (Patient patient : userRepository.findPatients()) {
            result.add(new PatientOptionDto(
                    patient.getId(),
                    patient.getFullName(),
                    patient.getUsername()
            ));
        }
        return Response.ok("Pacientes encontrados.", result);
    }

    public Response<List<AppointmentTableDto>> getPatientAppointmentTable(long patientId) {
        List<Appointment> appointments = new ArrayList<>();
        for (Appointment appointment : appointmentRepository.findAll()) {
            if (appointment.getPatient().getId() == patientId) {
                appointments.add(appointment);
            }
        }
        return Response.ok("Citas del paciente encontradas.", toSortedAppointmentDtos(appointments));
    }

    public Response<List<AppointmentTableDto>> getDoctorAppointmentTable(long doctorId, boolean pendingOnly) {
        List<Appointment> appointments = new ArrayList<>();
        for (Appointment appointment : appointmentRepository.findAll()) {
            boolean belongsToDoctor = appointment.getDoctor().getId() == doctorId;
            boolean hasRequestedStatus = !pendingOnly || appointment.getStatus() == AppointmentStatus.PENDING;
            if (belongsToDoctor && hasRequestedStatus) {
                appointments.add(appointment);
            }
        }
        return Response.ok("Citas del doctor encontradas.", toSortedAppointmentDtos(appointments));
    }

    public Response<List<HospitalizationTableDto>> getHospitalizationTable() {
        List<HospitalizationTableDto> result = new ArrayList<>();
        for (Hospitalization hospitalization : hospitalizationRepository.findAll()) {
            result.add(toHospitalizationDto(hospitalization));
        }
        return Response.ok("Hospitalizaciones encontradas.", result);
    }

    private List<AppointmentTableDto> toSortedAppointmentDtos(List<Appointment> appointments) {
        appointments.sort(Comparator.comparing(Appointment::getDatetime).reversed());
        List<AppointmentTableDto> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            result.add(toAppointmentDto(appointment));
        }
        return result;
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

    private HospitalizationTableDto toHospitalizationDto(Hospitalization hospitalization) {
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
