package com.example.parking.auth.application.service;

import com.example.parking.auth.adapter.in.web.dto.LoginRequest;
import com.example.parking.auth.adapter.in.web.dto.LoginResponse;
import com.example.parking.auth.adapter.in.web.dto.RegisterRequest;
import com.example.parking.auth.adapter.in.web.dto.RegisterResponse;
import com.example.parking.auth.application.port.in.ICreateAccountUseCase;
import com.example.parking.auth.application.port.in.IGetAccountUseCase;
import com.example.parking.auth.application.port.in.ILoginUseCase;
import com.example.parking.auth.application.port.in.ILockAccountUseCase;
import com.example.parking.auth.application.port.in.IRegisterUserUseCase;
import com.example.parking.auth.application.port.in.IUnlockAccountUseCase;
import com.example.parking.auth.application.port.out.IAuthUserRepositoryPort;
import com.example.parking.auth.application.port.out.IJwtTokenProviderPort;
import com.example.parking.auth.application.port.out.IPasswordEncoderPort;
import com.example.parking.auth.domain.model.AuthUser;
import com.example.parking.auth.domain.model.Role;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements
        IRegisterUserUseCase,
        ILoginUseCase,
        IGetAccountUseCase,
        ICreateAccountUseCase,
        ILockAccountUseCase,
        IUnlockAccountUseCase {

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
        String username = normalizeUsername(request.getUsername());

        if (authUserRepositoryPort.existsByUsername(username)) {
            throw new BusinessException("Username already exists");
        }

        if (request.getRole() != null && request.getRole() != Role.USER) {
            throw new BusinessException("Public registration only allows USER role");
        }

        String passwordHash = passwordEncoderPort.encode(request.getPassword());

        AuthUser newUser = new AuthUser(
                null,
                username,
                passwordHash,
                Role.USER,
                true
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
        String username = normalizeUsername(request.getUsername());

        AuthUser user = authUserRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!user.isEnabled()) {
            throw new BusinessException("Account is locked");
        }

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

    @Override
    public List<AuthUser> getAllAccounts() {
        return authUserRepositoryPort.findAll();
    }

    @Override
    public AuthUser createAccount(String username, String password, Role role) {
        String normalizedUsername = normalizeUsername(username);

        if (authUserRepositoryPort.existsByUsername(normalizedUsername)) {
            throw new BusinessException("Username already exists");
        }

        if (role == null) {
            throw new BusinessException("Role is required");
        }

        if (role == Role.USER) {
            throw new BusinessException("Admin account management only allows ADMIN or STAFF role");
        }

        String passwordHash = passwordEncoderPort.encode(password);

        AuthUser newAccount = new AuthUser(
                null,
                normalizedUsername,
                passwordHash,
                role,
                true
        );

        return authUserRepositoryPort.save(newAccount);
    }

    @Override
    public AuthUser lockAccount(Long accountId, Long currentAdminId) {
        AuthUser account = authUserRepositoryPort.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (currentAdminId != null && account.getId().equals(currentAdminId)) {
            throw new BusinessException("Admin cannot lock own account");
        }

        if (!account.isEnabled()) {
            throw new BusinessException("Account is already locked");
        }

        AuthUser lockedAccount = new AuthUser(
                account.getId(),
                account.getUsername(),
                account.getPasswordHash(),
                account.getRole(),
                false
        );

        return authUserRepositoryPort.save(lockedAccount);
    }

    @Override
    public AuthUser unlockAccount(Long accountId) {
        AuthUser account = authUserRepositoryPort.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.isEnabled()) {
            throw new BusinessException("Account is already active");
        }

        AuthUser unlockedAccount = new AuthUser(
                account.getId(),
                account.getUsername(),
                account.getPasswordHash(),
                account.getRole(),
                true
        );

        return authUserRepositoryPort.save(unlockedAccount);
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("Username is required");
        }

        return username.trim();
    }
}