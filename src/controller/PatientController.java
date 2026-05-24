package controller;

import dto.PatientCreateRequest;
import dto.PatientProfileDto;
import dto.PatientUpdateRequest;
import response.Response;
import service.PatientService;

public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    public Response<PatientProfileDto> createPatient(PatientCreateRequest request) {
        return patientService.createPatient(request);
    }

    public Response<PatientProfileDto> updatePatient(PatientUpdateRequest request) {
        return patientService.updatePatient(request);
    }

    public Response<PatientProfileDto> getPatientProfile(String id) {
        return patientService.getPatientProfile(id);
    }
}
