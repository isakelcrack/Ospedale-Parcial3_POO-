package controller;

import dto.AppointmentTableDto;
import dto.PrescriptionRequest;
import response.Response;
import service.PrescriptionService;

public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    public Response<AppointmentTableDto> prescribe(PrescriptionRequest request) {
        return prescriptionService.prescribe(request);
    }
}
