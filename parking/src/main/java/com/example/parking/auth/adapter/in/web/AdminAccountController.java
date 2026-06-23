package com.example.parking.auth.adapter.in.web;

import com.example.parking.auth.adapter.in.web.dto.AdminAccountResponse;
import com.example.parking.auth.adapter.in.web.dto.CreateAccountRequest;
import com.example.parking.auth.application.port.in.ICreateAccountUseCase;
import com.example.parking.auth.application.port.in.IGetAccountUseCase;
import com.example.parking.auth.application.port.in.ILockAccountUseCase;
import com.example.parking.auth.application.port.in.IUnlockAccountUseCase;
import com.example.parking.auth.domain.model.AuthUser;
import com.example.parking.shared.response.ApiResponse;
import com.example.parking.shared.security.CurrentUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminAccountController {

    private final IGetAccountUseCase getAccountUseCase;
    private final ICreateAccountUseCase createAccountUseCase;
    private final ILockAccountUseCase lockAccountUseCase;
    private final IUnlockAccountUseCase unlockAccountUseCase;

    public AdminAccountController(
            IGetAccountUseCase getAccountUseCase,
            ICreateAccountUseCase createAccountUseCase,
            ILockAccountUseCase lockAccountUseCase,
            IUnlockAccountUseCase unlockAccountUseCase
    ) {
        this.getAccountUseCase = getAccountUseCase;
        this.createAccountUseCase = createAccountUseCase;
        this.lockAccountUseCase = lockAccountUseCase;
        this.unlockAccountUseCase = unlockAccountUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminAccountResponse>>> getAllAccounts() {
        List<AdminAccountResponse> accounts = getAccountUseCase.getAllAccounts()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get accounts successfully", accounts)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminAccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        AuthUser account = createAccountUseCase.createAccount(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Create account successfully", toResponse(account))
        );
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> lockAccount(
            @PathVariable Long id,
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }

        AuthUser account = lockAccountUseCase.lockAccount(id, currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success("Lock account successfully", toResponse(account))
        );
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> unlockAccount(
            @PathVariable Long id
    ) {
        AuthUser account = unlockAccountUseCase.unlockAccount(id);

        return ResponseEntity.ok(
                ApiResponse.success("Unlock account successfully", toResponse(account))
        );
    }

    private CurrentUserPrincipal getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CurrentUserPrincipal currentUser)) {
            return null;
        }

        return currentUser;
    }

    private AdminAccountResponse toResponse(AuthUser account) {
        return new AdminAccountResponse(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                account.isEnabled()
        );
    }
}