package controller;

import dto.HospitalizationRequest;
import dto.HospitalizationTableDto;
import response.Response;
import service.HospitalizationService;

public class HospitalizationController {

    private final HospitalizationService hospitalizationService;

    public HospitalizationController(HospitalizationService hospitalizationService) {
        this.hospitalizationService = hospitalizationService;
    }

    public Response<HospitalizationTableDto> requestHospitalization(HospitalizationRequest request) {
        return hospitalizationService.requestHospitalization(request);
    }

    public Response<HospitalizationTableDto> approveHospitalization(String hospitalizationId) {
        return hospitalizationService.approveHospitalization(hospitalizationId);
    }

    public Response<HospitalizationTableDto> cancelHospitalization(String hospitalizationId) {
        return hospitalizationService.cancelHospitalization(hospitalizationId);
    }

    public Response<HospitalizationTableDto> denyHospitalization(String hospitalizationId) {
        return hospitalizationService.denyHospitalization(hospitalizationId);
    }

    public Response<HospitalizationTableDto> createHospitalizationFromAppointment(HospitalizationRequest request) {
        return hospitalizationService.createHospitalizationFromAppointment(request);
    }
}
