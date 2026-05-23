package repository;

import java.util.List;
import java.util.Optional;
import model.Appointment;

public interface AppointmentRepository {

    List<Appointment> findAll();

    Optional<Appointment> findById(String id);

    void save(Appointment appointment);

    int countByPatientId(long patientId);
}
