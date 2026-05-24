package dto;

public class AppointmentUpdateRequest {

    private String appointmentId;
    private String doctorId;
    private String time;
    private String reason;
    private boolean inPerson;
    private String diagnosis;
    private String observations;
    private String recommendedTreatment;
    private String followUp;

    public AppointmentUpdateRequest() {
    }

    public AppointmentUpdateRequest(String appointmentId, String doctorId, String time,
            String reason, boolean inPerson, String diagnosis, String observations,
            String recommendedTreatment, String followUp) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.time = time;
        this.reason = reason;
        this.inPerson = inPerson;
        this.diagnosis = diagnosis;
        this.observations = observations;
        this.recommendedTreatment = recommendedTreatment;
        this.followUp = followUp;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getRecommendedTreatment() {
        return recommendedTreatment;
    }

    public void setRecommendedTreatment(String recommendedTreatment) {
        this.recommendedTreatment = recommendedTreatment;
    }

    public String getFollowUp() {
        return followUp;
    }

    public void setFollowUp(String followUp) {
        this.followUp = followUp;
    }
}
