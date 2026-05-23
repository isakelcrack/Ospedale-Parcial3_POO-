package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Hospitalization;

public class InMemoryHospitalizationRepository implements HospitalizationRepository {

    private final List<Hospitalization> hospitalizations = new ArrayList<>();
    private final ModelEventPublisher publisher;

    public InMemoryHospitalizationRepository(ModelEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public List<Hospitalization> findAll() {
        return new ArrayList<>(hospitalizations);
    }

    @Override
    public Optional<Hospitalization> findById(String id) {
        return hospitalizations.stream()
                .filter(hospitalization -> hospitalization.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(Hospitalization hospitalization) {
        findById(hospitalization.getId()).ifPresentOrElse(existing -> {
            int index = hospitalizations.indexOf(existing);
            hospitalizations.set(index, hospitalization);
        }, () -> hospitalizations.add(hospitalization));
        publisher.notifyChange();
    }

    @Override
    public int countByPatientId(long patientId) {
        return (int) hospitalizations.stream()
                .filter(hospitalization -> hospitalization.getPatient().getId() == patientId)
                .count();
    }
}
