package controller;

import dto.LoginRequest;
import dto.SessionDto;
import response.Response;
import service.AuthService;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Response<SessionDto> login(LoginRequest request) {
        return authService.login(request);
    }
}
