package service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;
import model.User;
import repository.UserRepository;
import response.Response;

public class ValidationService {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("[1-9]\\d{11}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{10}");
    private static final Pattern LICENCE_PATTERN = Pattern.compile("L-\\d{10} MTL");
    private static final Pattern OFFICE_PATTERN = Pattern.compile("O-\\d{3}");
    private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserRepository userRepository;

    public ValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isValidUserId(String id) {
        return id != null && USER_ID_PATTERN.matcher(id.trim()).matches();
    }

    public Response<Void> validateUserId(String id) {
        if (!isValidUserId(id)) {
            return Response.badRequest("El id debe ser mayor que 0 y tener exactamente 12 digitos.");
        }
        return Response.ok("Id valido.", null);
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public Response<Void> validateEmail(String email) {
        if (!isValidEmail(email)) {
            return Response.badRequest("El email debe tener el formato XXXXX@XXXXX.com.");
        }
        return Response.ok("Email valido.", null);
    }

    public boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public Response<Void> validatePhone(String phone) {
        if (!isValidPhone(phone)) {
            return Response.badRequest("El telefono debe tener exactamente 10 digitos.");
        }
        return Response.ok("Telefono valido.", null);
    }

    public boolean isValidLicenceNumber(String licenceNumber) {
        return licenceNumber != null && LICENCE_PATTERN.matcher(licenceNumber.trim()).matches();
    }

    public Response<Void> validateLicenceNumber(String licenceNumber) {
        if (!isValidLicenceNumber(licenceNumber)) {
            return Response.badRequest("La licencia debe tener el formato L-XXXXXXXXXX MTL.");
        }
        return Response.ok("Licencia valida.", null);
    }

    public boolean isValidAssignedOffice(String assignedOffice) {
        return assignedOffice != null && OFFICE_PATTERN.matcher(assignedOffice.trim()).matches();
    }

    public Response<Void> validateAssignedOffice(String assignedOffice) {
        if (!isValidAssignedOffice(assignedOffice)) {
            return Response.badRequest("La oficina debe tener el formato O-XXX.");
        }
        return Response.ok("Oficina valida.", null);
    }

    public boolean isValidDate(String date) {
        if (date == null || !DATE_PATTERN.matcher(date.trim()).matches()) {
            return false;
        }
        try {
            LocalDate.parse(date.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public Response<Void> validateDate(String date) {
        if (!isValidDate(date)) {
            return Response.badRequest("La fecha debe tener formato yyyy-MM-dd y ser valida.");
        }
        return Response.ok("Fecha valida.", null);
    }

    public boolean isValidQuarterHour(String time) {
        if (time == null || !TIME_PATTERN.matcher(time.trim()).matches()) {
            return false;
        }
        try {
            LocalTime parsedTime = LocalTime.parse(time.trim(), TIME_FORMATTER);
            int minute = parsedTime.getMinute();
            return minute == 0 || minute == 15 || minute == 30 || minute == 45;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public Response<Void> validateQuarterHour(String time) {
        if (!isValidQuarterHour(time)) {
            return Response.badRequest("La hora debe tener formato HH:mm y minutos 00, 15, 30 o 45.");
        }
        return Response.ok("Hora valida.", null);
    }

    public boolean passwordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    public Response<Void> validatePasswordConfirmation(String password, String confirmPassword) {
        if (!passwordsMatch(password, confirmPassword)) {
            return Response.badRequest("La contrasena y su confirmacion deben coincidir.");
        }
        return Response.ok("Contrasena confirmada.", null);
    }

    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(username.trim()).isEmpty();
    }

    public Response<Void> validateUniqueUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Response.badRequest("El nombre de usuario es obligatorio.");
        }
        if (!isUsernameAvailable(username)) {
            return Response.badRequest("El nombre de usuario ya existe.");
        }
        return Response.ok("Nombre de usuario disponible.", null);
    }

    public boolean isUsernameAvailableForUpdate(String username, long currentUserId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        Optional<User> existingUser = userRepository.findByUsername(username.trim());
        return existingUser.isEmpty() || existingUser.get().getId() == currentUserId;
    }

    public Response<Void> validateUniqueUsername(String username, long currentUserId) {
        if (username == null || username.trim().isEmpty()) {
            return Response.badRequest("El nombre de usuario es obligatorio.");
        }
        if (!isUsernameAvailableForUpdate(username, currentUserId)) {
            return Response.badRequest("El nombre de usuario ya existe.");
        }
        return Response.ok("Nombre de usuario disponible.", null);
    }
}
