package com.example.parking.auth.adapter.in.web;

import com.example.parking.auth.adapter.in.web.dto.LoginRequest;
import com.example.parking.auth.adapter.in.web.dto.LoginResponse;
import com.example.parking.auth.adapter.in.web.dto.RegisterRequest;
import com.example.parking.auth.adapter.in.web.dto.RegisterResponse;
import com.example.parking.auth.application.port.in.ILoginUseCase;
import com.example.parking.auth.application.port.in.IRegisterUserUseCase;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IRegisterUserUseCase registerUserUseCase;
    private final ILoginUseCase loginUseCase;

    public AuthController(
            IRegisterUserUseCase registerUserUseCase,
            ILoginUseCase loginUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = registerUserUseCase.register(request);

        return ResponseEntity.ok(
                ApiResponse.success("Register successfully", response)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginUseCase.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successfully", response)
        );
    }
}