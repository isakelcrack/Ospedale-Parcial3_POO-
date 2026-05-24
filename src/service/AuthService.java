package service;

import dto.LoginRequest;
import dto.SessionDto;
import java.util.Optional;
import model.Administrator;
import model.Doctor;
import model.Patient;
import model.User;
import repository.UserRepository;
import response.Response;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response<SessionDto> login(LoginRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos de login son obligatorios.");
        }
        String username = request.getUsername();
        String password = request.getPassword();
        if (username == null || username.trim().isEmpty()) {
            return Response.badRequest("El nombre de usuario es obligatorio.");
        }
        if (password == null || password.isEmpty()) {
            return Response.badRequest("La contrasena es obligatoria.");
        }

        Optional<User> user = userRepository.findByUsername(username.trim());
        if (user.isEmpty()) {
            return Response.notFound("Usuario no encontrado.");
        }
        if (!password.equals(user.get().getPassword())) {
            return Response.badRequest("Credenciales invalidas.");
        }
        return Response.ok("Login exitoso.", toSessionDto(user.get()));
    }

    private SessionDto toSessionDto(User user) {
        return new SessionDto(
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getFullName(),
                resolveRole(user)
        );
    }

    private String resolveRole(User user) {
        if (user instanceof Administrator) {
            return "ADMIN";
        }
        if (user instanceof Patient) {
            return "PATIENT";
        }
        if (user instanceof Doctor) {
            return "DOCTOR";
        }
        return "USER";
    }
}
