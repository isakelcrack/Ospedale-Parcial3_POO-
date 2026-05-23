/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

public class Hospitalization {

    private final String id;
    private final Patient patient;
    private final Doctor doctor;
    private LocalDate date;
    private String reason;
    private RoomType roomType;
    private String observations;
    private HospitalizationStatus status;

    public Hospitalization(String id, Patient patient, Doctor doctor, LocalDate date,
            String reason, RoomType roomType, String observations) {
        this(id, patient, doctor, date, reason, roomType, observations, HospitalizationStatus.REQUESTED);
    }

    public Hospitalization(String id, Patient patient, Doctor doctor, LocalDate date,
            String reason, RoomType roomType, String observations, HospitalizationStatus status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.reason = reason;
        this.roomType = roomType;
        this.observations = observations;
        this.status = status;
        patient.setHospitalization(this);
        doctor.addHospitalization(this);
    }

    public String getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public HospitalizationStatus getStatus() {
        return status;
    }

    public void setStatus(HospitalizationStatus status) {
        this.status = status;
    }
}

