package controller;

import dto.AppointmentRequest;
import dto.AppointmentTableDto;
import dto.AppointmentUpdateRequest;
import java.util.List;
import response.Response;
import service.AppointmentService;

public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public Response<AppointmentTableDto> requestAppointment(AppointmentRequest request) {
        return appointmentService.requestAppointment(request);
    }

    public Response<AppointmentTableDto> acceptAppointment(String appointmentId) {
        return appointmentService.acceptAppointment(appointmentId);
    }

    public Response<AppointmentTableDto> acceptAppointment(String appointmentId, String doctorId) {
        return appointmentService.acceptAppointment(appointmentId, doctorId);
    }

    public Response<AppointmentTableDto> cancelAppointment(String appointmentId) {
        return appointmentService.cancelAppointment(appointmentId);
    }

    public Response<AppointmentTableDto> rescheduleAppointment(AppointmentUpdateRequest request) {
        return appointmentService.rescheduleAppointment(request);
    }

    public Response<AppointmentTableDto> completeAppointment(AppointmentUpdateRequest request) {
        return appointmentService.completeAppointment(request);
    }

    public Response<List<AppointmentTableDto>> getPatientAppointments(String patientId) {
        return appointmentService.getPatientAppointments(patientId);
    }

    public Response<List<AppointmentTableDto>> getDoctorAppointments(String doctorId, boolean pendingOnly) {
        return appointmentService.getDoctorAppointments(doctorId, pendingOnly);
    }
}
