package com.example.parking.vehicle.adapter.in.web;

import com.example.parking.shared.response.ApiResponse;
import com.example.parking.vehicle.adapter.in.web.dto.CreateVehicleRequest;
import com.example.parking.vehicle.adapter.in.web.dto.UpdateVehicleRequest;
import com.example.parking.vehicle.adapter.in.web.dto.VehicleResponse;
import com.example.parking.vehicle.application.port.in.ICreateVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IDeleteVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IGetVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IUpdateVehicleUseCase;
import com.example.parking.vehicle.domain.model.Vehicle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
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

    public VehicleController(
            ICreateVehicleUseCase createVehicleUseCase,
            IGetVehicleUseCase getVehicleUseCase,
            IUpdateVehicleUseCase updateVehicleUseCase,
            IDeleteVehicleUseCase deleteVehicleUseCase
    ) {
        this.createVehicleUseCase = createVehicleUseCase;
        this.getVehicleUseCase = getVehicleUseCase;
        this.updateVehicleUseCase = updateVehicleUseCase;
        this.deleteVehicleUseCase = deleteVehicleUseCase;
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
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles() {
        List<VehicleResponse> vehicles = getVehicleUseCase.getAllVehicles()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get vehicles successfully", vehicles)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleByPlateNumber(
            @RequestParam @NotBlank(message = "Plate number is required") String plateNumber
    ) {
        Vehicle vehicle = getVehicleUseCase.getVehicleByPlateNumber(plateNumber);

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

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                vehicle.getOwnerName()
        );
    }
}