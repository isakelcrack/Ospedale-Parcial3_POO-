package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Doctor;
import model.Patient;
import model.User;

public class InMemoryUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();
    private final ModelEventPublisher publisher;

    public InMemoryUserRepository(ModelEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public List<Patient> findPatients() {
        return users.stream()
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .toList();
    }

    @Override
    public List<Doctor> findDoctors() {
        return users.stream()
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast)
                .toList();
    }

    @Override
    public Optional<User> findById(long id) {
        return users.stream().filter(user -> user.getId() == id).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public void save(User user) {
        findById(user.getId()).ifPresentOrElse(existing -> {
            int index = users.indexOf(existing);
            users.set(index, user);
        }, () -> users.add(user));
        publisher.notifyChange();
    }
}
