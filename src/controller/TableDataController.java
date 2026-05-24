package controller;

import dto.AppointmentTableDto;
import dto.DoctorOptionDto;
import dto.HospitalizationTableDto;
import dto.PatientOptionDto;
import java.util.List;
import response.Response;
import service.TableDataService;

public class TableDataController {

    private final TableDataService tableDataService;

    public TableDataController(TableDataService tableDataService) {
        this.tableDataService = tableDataService;
    }

    public Response<List<DoctorOptionDto>> getDoctorOptions() {
        return tableDataService.getDoctorOptions();
    }

    public Response<List<PatientOptionDto>> getPatientOptions() {
        return tableDataService.getPatientOptions();
    }

    public Response<List<AppointmentTableDto>> getPatientAppointmentTable(long patientId) {
        return tableDataService.getPatientAppointmentTable(patientId);
    }

    public Response<List<AppointmentTableDto>> getDoctorAppointmentTable(long doctorId, boolean pendingOnly) {
        return tableDataService.getDoctorAppointmentTable(doctorId, pendingOnly);
    }

    public Response<List<HospitalizationTableDto>> getHospitalizationTable() {
        return tableDataService.getHospitalizationTable();
    }
}
