package controller;

import dto.DoctorCreateRequest;
import dto.DoctorProfileDto;
import dto.DoctorUpdateRequest;
import dto.SessionDto;
import response.Response;
import service.DoctorService;

public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    public Response<DoctorProfileDto> createDoctor(DoctorCreateRequest request, SessionDto currentSession) {
        return doctorService.createDoctor(request, currentSession);
    }

    public Response<DoctorProfileDto> updateDoctor(DoctorUpdateRequest request) {
        return doctorService.updateDoctor(request);
    }

    public Response<DoctorProfileDto> getDoctorProfile(String id) {
        return doctorService.getDoctorProfile(id);
    }
}
