package dto;

public class AppointmentRequest {

    private String patientId;
    private String doctorId;
    private String specialty;
    private String date;
    private String time;
    private String reason;
    private boolean inPerson;

    public AppointmentRequest() {
    }

    public AppointmentRequest(String patientId, String doctorId, String specialty, String date,
            String time, String reason, boolean inPerson) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.specialty = specialty;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.inPerson = inPerson;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isInPerson() {
        return inPerson;
    }

    public void setInPerson(boolean inPerson) {
        this.inPerson = inPerson;
    }
}
