package service;

import dto.DoctorCreateRequest;
import dto.DoctorProfileDto;
import dto.DoctorUpdateRequest;
import dto.SessionDto;
import java.util.Optional;
import model.Administrator;
import model.Doctor;
import model.Specialty;
import model.User;
import repository.UserRepository;
import response.Response;

public class DoctorService {

    private final UserRepository userRepository;
    private final ValidationService validationService;

    public DoctorService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public Response<DoctorProfileDto> createDoctor(DoctorCreateRequest request, SessionDto currentSession) {
        if (!isAdmin(currentSession)) {
            return Response.forbidden("Solo un administrador puede registrar doctores.");
        }
        if (request == null) {
            return Response.badRequest("Los datos del doctor son obligatorios.");
        }

        Response<Void> validation = validateDoctorData(
                request.getId(),
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getSpecialty(),
                request.getLicenceNumber(),
                request.getAssignedOffice(),
                false
        );
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Doctor doctor = new Doctor(
                Long.parseLong(request.getId().trim()),
                request.getUsername().trim(),
                request.getFirstname(),
                request.getLastname(),
                request.getPassword(),
                Specialty.fromText(request.getSpecialty()),
                request.getLicenceNumber().trim(),
                request.getAssignedOffice().trim()
        );
        userRepository.save(doctor);

        return Response.created("Doctor creado exitosamente.", toProfileDto(doctor));
    }

    public Response<DoctorProfileDto> updateDoctor(DoctorUpdateRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos del doctor son obligatorios.");
        }

        Response<Void> idValidation = validationService.validateUserId(request.getId());
        if (!idValidation.isSuccess()) {
            return Response.badRequest(idValidation.getMessage());
        }

        long id = Long.parseLong(request.getId().trim());
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty() || !(user.get() instanceof Doctor)) {
            return Response.notFound("Doctor no encontrado.");
        }

        Response<Void> validation = validateDoctorData(
                request.getId(),
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getSpecialty(),
                request.getLicenceNumber(),
                request.getAssignedOffice(),
                true
        );
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Doctor doctor = (Doctor) user.get();
        doctor.setUsername(request.getUsername().trim());
        doctor.setFirstname(request.getFirstname());
        doctor.setLastname(request.getLastname());
        doctor.setPassword(request.getPassword());
        doctor.setSpecialty(Specialty.fromText(request.getSpecialty()));
        doctor.setLicenceNumber(request.getLicenceNumber().trim());
        doctor.setAssignedOffice(request.getAssignedOffice().trim());
        userRepository.save(doctor);

        return Response.ok("Doctor actualizado exitosamente.", toProfileDto(doctor));
    }

    public Response<DoctorProfileDto> getDoctorProfile(String id) {
        Response<Void> idValidation = validationService.validateUserId(id);
        if (!idValidation.isSuccess()) {
            return Response.badRequest(idValidation.getMessage());
        }

        Optional<User> user = userRepository.findById(Long.parseLong(id.trim()));
        if (user.isEmpty() || !(user.get() instanceof Doctor)) {
            return Response.notFound("Doctor no encontrado.");
        }
        return Response.ok("Perfil de doctor encontrado.", toProfileDto((Doctor) user.get()));
    }

    private Response<Void> validateDoctorData(String id, String username, String password,
            String confirmPassword, String specialty, String licenceNumber, String assignedOffice,
            boolean update) {
        Response<Void> validation = validationService.validateUserId(id);
        if (!validation.isSuccess()) {
            return validation;
        }

        long parsedId = Long.parseLong(id.trim());
        if (!update && userRepository.findById(parsedId).isPresent()) {
            return Response.badRequest("El id ya existe.");
        }

        validation = update
                ? validationService.validateUniqueUsername(username, parsedId)
                : validationService.validateUniqueUsername(username);
        if (!validation.isSuccess()) {
            return validation;
        }

        validation = validationService.validatePasswordConfirmation(password, confirmPassword);
        if (!validation.isSuccess()) {
            return validation;
        }

        try {
            Specialty.fromText(specialty);
        } catch (IllegalArgumentException ex) {
            return Response.badRequest("La especialidad no es valida.");
        }

        validation = validationService.validateLicenceNumber(licenceNumber);
        if (!validation.isSuccess()) {
            return validation;
        }

        return validationService.validateAssignedOffice(assignedOffice);
    }

    private boolean isAdmin(SessionDto currentSession) {
        if (currentSession == null) {
            return false;
        }
        Optional<User> user = userRepository.findById(currentSession.getId());
        return user.isPresent() && user.get() instanceof Administrator;
    }

    private DoctorProfileDto toProfileDto(Doctor doctor) {
        return new DoctorProfileDto(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getFirstname(),
                doctor.getLastname(),
                doctor.getFullName(),
                doctor.getSpecialty().displayName(),
                doctor.getLicenceNumber(),
                doctor.getAssignedOffice()
        );
    }
}
