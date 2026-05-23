package repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import model.Administrator;
import model.Doctor;
import model.Patient;
import model.Specialty;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUserLoader {

    private final UserRepository userRepository;

    public JsonUserLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void load(Path path) {
        if (!Files.exists(path)) {
            userRepository.save(new Administrator(100000000001L, "admin_root", "Carlos", "Mendoza", "Admin@1234"));
            return;
        }
        try {
            JSONObject root = new JSONObject(Files.readString(path));
            JSONArray users = root.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                JSONObject item = users.getJSONObject(i);
                String type = item.getString("type");
                long id = item.getLong("id");
                String username = item.getString("username");
                String firstname = item.getString("firstname");
                String lastname = item.getString("lastname");
                String password = item.getString("password");
                switch (type) {
                    case "admin" -> userRepository.save(new Administrator(id, username, firstname, lastname, password));
                    case "patient" -> userRepository.save(new Patient(id, username, firstname, lastname, password,
                            item.getString("email"), LocalDate.parse(item.getString("birthdate")),
                            item.getBoolean("gender"), item.get("phone").toString(), item.getString("address")));
                    case "doctor" -> userRepository.save(new Doctor(id, username, firstname, lastname, password,
                            Specialty.fromText(item.getString("specialty")),
                            item.getString("licenceNumber"), item.getString("assignedOffice")));
                    default -> throw new IllegalArgumentException("Unknown user type: " + type);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read JSON file: " + path, ex);
        }
    }
}
