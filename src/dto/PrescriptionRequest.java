package dto;

public class PrescriptionRequest {

    private String appointmentId;
    private String doctorId;
    private String medicationName;
    private String dose;
    private String administrationRoute;
    private String treatmentDuration;
    private String additionalInstructions;
    private String frequency;

    public PrescriptionRequest() {
    }

    public PrescriptionRequest(String appointmentId, String medicationName, String dose,
            String administrationRoute, String treatmentDuration, String additionalInstructions,
            String frequency) {
        this(appointmentId, null, medicationName, dose, administrationRoute,
                treatmentDuration, additionalInstructions, frequency);
    }

    public PrescriptionRequest(String appointmentId, String doctorId, String medicationName, String dose,
            String administrationRoute, String treatmentDuration, String additionalInstructions,
            String frequency) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.medicationName = medicationName;
        this.dose = dose;
        this.administrationRoute = administrationRoute;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstructions = additionalInstructions;
        this.frequency = frequency;
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

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getAdministrationRoute() {
        return administrationRoute;
    }

    public void setAdministrationRoute(String administrationRoute) {
        this.administrationRoute = administrationRoute;
    }

    public String getTreatmentDuration() {
        return treatmentDuration;
    }

    public void setTreatmentDuration(String treatmentDuration) {
        this.treatmentDuration = treatmentDuration;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void setAdditionalInstructions(String additionalInstructions) {
        this.additionalInstructions = additionalInstructions;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
