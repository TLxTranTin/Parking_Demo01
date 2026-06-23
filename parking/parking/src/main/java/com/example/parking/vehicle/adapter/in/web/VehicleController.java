package com.example.parking.vehicle.adapter.in.web;

import com.example.parking.shared.response.ApiResponse;
import com.example.parking.shared.security.CurrentUserPrincipal;
import com.example.parking.vehicle.adapter.in.web.dto.CreateVehicleRequest;
import com.example.parking.vehicle.adapter.in.web.dto.RegisterVehicleRequest;
import com.example.parking.vehicle.adapter.in.web.dto.UpdateVehicleRequest;
import com.example.parking.vehicle.adapter.in.web.dto.VehicleResponse;
import com.example.parking.vehicle.application.port.in.IApproveVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IBlockVehicleUseCase;
import com.example.parking.vehicle.application.port.in.ICreateVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IDeleteVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IGetVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IRegisterVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IRejectVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IUnblockVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IUpdateVehicleUseCase;
import com.example.parking.vehicle.domain.model.Vehicle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final ICreateVehicleUseCase createVehicleUseCase;
    private final IGetVehicleUseCase getVehicleUseCase;
    private final IUpdateVehicleUseCase updateVehicleUseCase;
    private final IDeleteVehicleUseCase deleteVehicleUseCase;
    private final IRegisterVehicleUseCase registerVehicleUseCase;
    private final IApproveVehicleUseCase approveVehicleUseCase;
    private final IRejectVehicleUseCase rejectVehicleUseCase;
    private final IBlockVehicleUseCase blockVehicleUseCase;
    private final IUnblockVehicleUseCase unblockVehicleUseCase;

    public VehicleController(
            ICreateVehicleUseCase createVehicleUseCase,
            IGetVehicleUseCase getVehicleUseCase,
            IUpdateVehicleUseCase updateVehicleUseCase,
            IDeleteVehicleUseCase deleteVehicleUseCase,
            IRegisterVehicleUseCase registerVehicleUseCase,
            IApproveVehicleUseCase approveVehicleUseCase,
            IRejectVehicleUseCase rejectVehicleUseCase,
            IBlockVehicleUseCase blockVehicleUseCase,
            IUnblockVehicleUseCase unblockVehicleUseCase
    ) {
        this.createVehicleUseCase = createVehicleUseCase;
        this.getVehicleUseCase = getVehicleUseCase;
        this.updateVehicleUseCase = updateVehicleUseCase;
        this.deleteVehicleUseCase = deleteVehicleUseCase;
        this.registerVehicleUseCase = registerVehicleUseCase;
        this.approveVehicleUseCase = approveVehicleUseCase;
        this.rejectVehicleUseCase = rejectVehicleUseCase;
        this.blockVehicleUseCase = blockVehicleUseCase;
        this.unblockVehicleUseCase = unblockVehicleUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VehicleResponse>> registerVehicle(
            @Valid @RequestBody RegisterVehicleRequest request,
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return unauthorized();
        }

        if (!isUser(currentUser)) {
            return forbidden("Only USER can register vehicle");
        }

        Vehicle vehicle = registerVehicleUseCase.registerVehicle(
                currentUser.getId(),
                currentUser.getUsername(),
                request.getPlateNumber(),
                request.getType(),
                request.getBrand(),
                request.getColor(),
                request.getDescription()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Register vehicle successfully", toResponse(vehicle))
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<VehicleResponse>> approveVehicle(
            @PathVariable Long id,
            Authentication authentication
    ) {
        ResponseEntity<ApiResponse<VehicleResponse>> authError = validateAdmin(authentication);

        if (authError != null) {
            return authError;
        }

        Vehicle vehicle = approveVehicleUseCase.approveVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.success("Approve vehicle successfully", toResponse(vehicle))
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<VehicleResponse>> rejectVehicle(
            @PathVariable Long id,
            Authentication authentication
    ) {
        ResponseEntity<ApiResponse<VehicleResponse>> authError = validateAdmin(authentication);

        if (authError != null) {
            return authError;
        }

        Vehicle vehicle = rejectVehicleUseCase.rejectVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.success("Reject vehicle successfully", toResponse(vehicle))
        );
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<ApiResponse<VehicleResponse>> blockVehicle(
            @PathVariable Long id,
            Authentication authentication
    ) {
        ResponseEntity<ApiResponse<VehicleResponse>> authError = validateAdmin(authentication);

        if (authError != null) {
            return authError;
        }

        Vehicle vehicle = blockVehicleUseCase.blockVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.success("Block vehicle successfully", toResponse(vehicle))
        );
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<ApiResponse<VehicleResponse>> unblockVehicle(
            @PathVariable Long id,
            Authentication authentication
    ) {
        ResponseEntity<ApiResponse<VehicleResponse>> authError = validateAdmin(authentication);

        if (authError != null) {
            return authError;
        }

        Vehicle vehicle = unblockVehicleUseCase.unblockVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.success("Unblock vehicle successfully", toResponse(vehicle))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request
    ) {
        Vehicle vehicle = createVehicleUseCase.createVehicle(
                request.getPlateNumber(),
                request.getType(),
                request.getOwnerName()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Create vehicle successfully", toResponse(vehicle))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehicles(
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return unauthorized();
        }

        List<VehicleResponse> vehicles = getVehicleUseCase.getVehiclesForCurrentUser(
                        currentUser.getId(),
                        currentUser.getRole()
                )
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get vehicles successfully", vehicles)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleByPlateNumber(
            @RequestParam @NotBlank(message = "Plate number is required") String plateNumber,
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return unauthorized();
        }

        if (isUser(currentUser)) {
            return forbidden("USER cannot search vehicle by plate number");
        }

        Vehicle vehicle = getVehicleUseCase.searchVehicleByPlateNumberForCurrentUser(
                plateNumber,
                currentUser.getId(),
                currentUser.getRole()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Get vehicle successfully", toResponse(vehicle))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        Vehicle vehicle = updateVehicleUseCase.updateVehicle(
                id,
                request.getPlateNumber(),
                request.getType(),
                request.getOwnerName()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Update vehicle successfully", toResponse(vehicle))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {
        deleteVehicleUseCase.deleteVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.success("Delete vehicle successfully", null)
        );
    }

    private ResponseEntity<ApiResponse<VehicleResponse>> validateAdmin(Authentication authentication) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return unauthorized();
        }

        if (!isAdmin(currentUser)) {
            return forbidden("Only ADMIN can manage vehicle status");
        }

        return null;
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

    private boolean isAdmin(CurrentUserPrincipal currentUser) {
        return "ADMIN".equals(currentUser.getRole());
    }

    private boolean isUser(CurrentUserPrincipal currentUser) {
        return "USER".equals(currentUser.getRole());
    }

    private <T> ResponseEntity<ApiResponse<T>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Unauthorized"));
    }

    private <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(message));
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                vehicle.getOwnerName(),
                vehicle.getOwnerUserId(),
                vehicle.getStatus(),
                vehicle.getBrand(),
                vehicle.getColor(),
                vehicle.getDescription()
        );
    }
}