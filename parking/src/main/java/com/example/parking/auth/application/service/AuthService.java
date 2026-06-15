package com.example.parking.auth.application.service;

import com.example.parking.auth.adapter.in.web.dto.LoginRequest;
import com.example.parking.auth.adapter.in.web.dto.LoginResponse;
import com.example.parking.auth.adapter.in.web.dto.RegisterRequest;
import com.example.parking.auth.adapter.in.web.dto.RegisterResponse;
import com.example.parking.auth.application.port.in.ILoginUseCase;
import com.example.parking.auth.application.port.in.IRegisterUserUseCase;
import com.example.parking.auth.application.port.out.IAuthUserRepositoryPort;
import com.example.parking.auth.application.port.out.IJwtTokenProviderPort;
import com.example.parking.auth.application.port.out.IPasswordEncoderPort;
import com.example.parking.auth.domain.model.AuthUser;
import com.example.parking.auth.domain.model.Role;
import com.example.parking.shared.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IRegisterUserUseCase, ILoginUseCase {

    private final IAuthUserRepositoryPort authUserRepositoryPort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IJwtTokenProviderPort jwtTokenProviderPort;

    public AuthService(
            IAuthUserRepositoryPort authUserRepositoryPort,
            IPasswordEncoderPort passwordEncoderPort,
            IJwtTokenProviderPort jwtTokenProviderPort
    ) {
        this.authUserRepositoryPort = authUserRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtTokenProviderPort = jwtTokenProviderPort;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (authUserRepositoryPort.existsByUsername(username)) {
            throw new BusinessException("Username already exists");
        }

        String passwordHash = passwordEncoderPort.encode(request.getPassword());

        Role role = request.getRole() == null ? Role.STAFF : request.getRole();

        AuthUser newUser = new AuthUser(
                null,
                username,
                passwordHash,
                role
        );

        AuthUser savedUser = authUserRepositoryPort.save(newUser);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().trim();

        AuthUser user = authUserRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        boolean passwordMatched = passwordEncoderPort.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatched) {
            throw new BusinessException("Invalid username or password");
        }

        String accessToken = jwtTokenProviderPort.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                "Bearer",
                accessToken
        );
    }
}