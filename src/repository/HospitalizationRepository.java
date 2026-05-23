package repository;

import java.util.List;
import java.util.Optional;
import model.Hospitalization;

public interface HospitalizationRepository {

    List<Hospitalization> findAll();

    Optional<Hospitalization> findById(String id);

    void save(Hospitalization hospitalization);

    int countByPatientId(long patientId);
}
