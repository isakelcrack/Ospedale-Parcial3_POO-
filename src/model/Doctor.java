/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {

    private Specialty specialty;
    private String licenceNumber;
    private String assignedOffice;
    private final List<Appointment> appointments;
    private final List<Hospitalization> hospitalizations;

    public Doctor(long id, String username, String firstname, String lastname, String password,
            Specialty specialty, String licenceNumber, String assignedOffice) {
        super(id, username, firstname, lastname, password);
        this.specialty = specialty;
        this.licenceNumber = licenceNumber;
        this.assignedOffice = assignedOffice;
        this.appointments = new ArrayList<>();
        this.hospitalizations = new ArrayList<>();
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        if (!appointments.contains(appointment)) {
            appointments.add(appointment);
        }
    }

    public List<Hospitalization> getHospitalizations() {
        return hospitalizations;
    }

    public boolean addHospitalization(Hospitalization hospitalization) {
        if (!hospitalizations.contains(hospitalization)) {
            return hospitalizations.add(hospitalization);
        }
        return false;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public void setAssignedOffice(String assignedOffice) {
        this.assignedOffice = assignedOffice;
    }
}

