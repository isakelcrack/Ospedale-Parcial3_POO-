package repository;

import java.util.List;
import java.util.Optional;
import model.Doctor;
import model.Patient;
import model.User;

public interface UserRepository {

    List<User> findAll();

    List<Patient> findPatients();

    List<Doctor> findDoctors();

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    void save(User user);
}
