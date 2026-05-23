package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Appointment;

public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();
    private final ModelEventPublisher publisher;

    public InMemoryAppointmentRepository(ModelEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return appointments.stream().filter(appointment -> appointment.getId().equals(id)).findFirst();
    }

    @Override
    public void save(Appointment appointment) {
        findById(appointment.getId()).ifPresentOrElse(existing -> {
            int index = appointments.indexOf(existing);
            appointments.set(index, appointment);
        }, () -> appointments.add(appointment));
        publisher.notifyChange();
    }

    @Override
    public int countByPatientId(long patientId) {
        return (int) appointments.stream()
                .filter(appointment -> appointment.getPatient().getId() == patientId)
                .count();
    }
}
