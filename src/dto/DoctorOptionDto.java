package dto;

public class DoctorOptionDto {

    private long id;
    private String fullName;
    private String specialty;
    private String assignedOffice;

    public DoctorOptionDto() {
    }

    public DoctorOptionDto(long id, String fullName, String specialty, String assignedOffice) {
        this.id = id;
        this.fullName = fullName;
        this.specialty = specialty;
        this.assignedOffice = assignedOffice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public void setAssignedOffice(String assignedOffice) {
        this.assignedOffice = assignedOffice;
    }
}
