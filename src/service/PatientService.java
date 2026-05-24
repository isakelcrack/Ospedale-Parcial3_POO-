package service;

import dto.PatientCreateRequest;
import dto.PatientProfileDto;
import dto.PatientUpdateRequest;
import java.time.LocalDate;
import java.util.Optional;
import model.Patient;
import model.User;
import repository.UserRepository;
import response.Response;

public class PatientService {

    private final UserRepository userRepository;
    private final ValidationService validationService;

    public PatientService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public Response<PatientProfileDto> createPatient(PatientCreateRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos del paciente son obligatorios.");
        }

        Response<Void> validation = validatePatientData(
                request.getId(),
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getEmail(),
                request.getBirthdate(),
                request.getPhone(),
                false
        );
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        long id = Long.parseLong(request.getId().trim());
        Patient patient = new Patient(
                id,
                request.getUsername().trim(),
                request.getFirstname(),
                request.getLastname(),
                request.getPassword(),
                request.getEmail().trim(),
                LocalDate.parse(request.getBirthdate().trim()),
                request.isGender(),
                request.getPhone().trim(),
                request.getAddress()
        );
        userRepository.save(patient);
        return Response.created("Paciente creado exitosamente.", toProfileDto(patient));
    }

    public Response<PatientProfileDto> updatePatient(PatientUpdateRequest request) {
        if (request == null) {
            return Response.badRequest("Los datos del paciente son obligatorios.");
        }

        Response<Void> idValidation = validationService.validateUserId(request.getId());
        if (!idValidation.isSuccess()) {
            return Response.badRequest(idValidation.getMessage());
        }

        long id = Long.parseLong(request.getId().trim());
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty() || !(user.get() instanceof Patient)) {
            return Response.notFound("Paciente no encontrado.");
        }

        Response<Void> validation = validatePatientData(
                request.getId(),
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getEmail(),
                request.getBirthdate(),
                request.getPhone(),
                true
        );
        if (!validation.isSuccess()) {
            return Response.badRequest(validation.getMessage());
        }

        Patient patient = (Patient) user.get();
        patient.setUsername(request.getUsername().trim());
        patient.setFirstname(request.getFirstname());
        patient.setLastname(request.getLastname());
        patient.setPassword(request.getPassword());
        patient.setEmail(request.getEmail().trim());
        patient.setBirthdate(LocalDate.parse(request.getBirthdate().trim()));
        patient.setGender(request.isGender());
        patient.setPhone(request.getPhone().trim());
        patient.setAddress(request.getAddress());
        userRepository.save(patient);

        return Response.ok("Paciente actualizado exitosamente.", toProfileDto(patient));
    }

    public Response<PatientProfileDto> getPatientProfile(String id) {
        Response<Void> idValidation = validationService.validateUserId(id);
        if (!idValidation.isSuccess()) {
            return Response.badRequest(idValidation.getMessage());
        }

        Optional<User> user = userRepository.findById(Long.parseLong(id.trim()));
        if (user.isEmpty() || !(user.get() instanceof Patient)) {
            return Response.notFound("Paciente no encontrado.");
        }
        return Response.ok("Perfil de paciente encontrado.", toProfileDto((Patient) user.get()));
    }

    private Response<Void> validatePatientData(String id, String username, String password,
            String confirmPassword, String email, String birthdate, String phone, boolean update) {
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

        validation = validationService.validateEmail(email);
        if (!validation.isSuccess()) {
            return validation;
        }

        validation = validationService.validateDate(birthdate);
        if (!validation.isSuccess()) {
            return validation;
        }

        return validationService.validatePhone(phone);
    }

    private PatientProfileDto toProfileDto(Patient patient) {
        return new PatientProfileDto(
                patient.getId(),
                patient.getUsername(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getFullName(),
                patient.getEmail(),
                patient.getBirthdate().toString(),
                patient.isGender(),
                patient.getPhone(),
                patient.getAddress()
        );
    }
}
