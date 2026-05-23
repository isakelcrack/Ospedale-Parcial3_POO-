package repository;

import java.util.ArrayList;
import java.util.List;

public class ModelEventPublisher {

    private final List<ModelChangeListener> listeners = new ArrayList<>();

    public void addListener(ModelChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ModelChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyChange() {
        for (ModelChangeListener listener : new ArrayList<>(listeners)) {
            listener.onModelChanged();
        }
    }
}
