package com.example.parking.parkingslot.adapter.in.web;

import com.example.parking.buildingstructure.application.port.in.IListZonesUseCase;
import com.example.parking.buildingstructure.domain.model.BuildingZone;
import com.example.parking.parkingslot.adapter.in.web.dto.CreateParkingSlotRequest;
import com.example.parking.parkingslot.adapter.in.web.dto.ParkingSlotResponse;
import com.example.parking.parkingslot.adapter.in.web.dto.UpdateParkingSlotStatusRequest;
import com.example.parking.parkingslot.application.port.in.ICreateParkingSlotUseCase;
import com.example.parking.parkingslot.application.port.in.IDeleteParkingSlotUseCase;
import com.example.parking.parkingslot.application.port.in.IGetParkingSlotUseCase;
import com.example.parking.parkingslot.application.port.in.IUpdateParkingSlotStatusUseCase;
import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-slots")
public class ParkingSlotController {

    private final ICreateParkingSlotUseCase createParkingSlotUseCase;
    private final IGetParkingSlotUseCase getParkingSlotUseCase;
    private final IUpdateParkingSlotStatusUseCase updateParkingSlotStatusUseCase;
    private final IDeleteParkingSlotUseCase deleteParkingSlotUseCase;
    private final IListZonesUseCase listZonesUseCase;

    public ParkingSlotController(
            ICreateParkingSlotUseCase createParkingSlotUseCase,
            IGetParkingSlotUseCase getParkingSlotUseCase,
            IUpdateParkingSlotStatusUseCase updateParkingSlotStatusUseCase,
            IDeleteParkingSlotUseCase deleteParkingSlotUseCase,
            IListZonesUseCase listZonesUseCase

    ) {
        this.createParkingSlotUseCase = createParkingSlotUseCase;
        this.getParkingSlotUseCase = getParkingSlotUseCase;
        this.updateParkingSlotStatusUseCase = updateParkingSlotStatusUseCase;
        this.deleteParkingSlotUseCase = deleteParkingSlotUseCase;
        this.listZonesUseCase = listZonesUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> createParkingSlot(
            @Valid @RequestBody CreateParkingSlotRequest request
    ) {
        ParkingSlot parkingSlot = createParkingSlotUseCase.createParkingSlot(
                request.getSlotCode(),
                request.getFloor(),
                request.getVehicleType(),
                request.getZoneId()
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Create parking slot successfully", toResponse(parkingSlot)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingSlotResponse>>> getAllParkingSlots() {
        List<ParkingSlotResponse> parkingSlots = getParkingSlotUseCase.getAllParkingSlots()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get parking slots successfully", parkingSlots)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ParkingSlotResponse>>> getAvailableParkingSlots() {
        List<ParkingSlotResponse> parkingSlots = getParkingSlotUseCase.getAvailableParkingSlots()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get available parking slots successfully", parkingSlots)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> updateParkingSlotStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateParkingSlotStatusRequest request
    ) {
        ParkingSlot parkingSlot = updateParkingSlotStatusUseCase.updateStatus(
                id,
                request.getStatus()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Update parking slot status successfully", toResponse(parkingSlot))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteParkingSlot(@PathVariable Long id) {
        deleteParkingSlotUseCase.deleteParkingSlot(id);

        return ResponseEntity.ok(
                ApiResponse.success("Delete parking slot successfully", null)
        );
    }

        private ParkingSlotResponse toResponse(ParkingSlot parkingSlot) {
        if (parkingSlot.getZoneId() == null) {
                return new ParkingSlotResponse(
                        parkingSlot.getId(),
                        parkingSlot.getSlotCode(),
                        parkingSlot.getFloor(),
                        parkingSlot.getVehicleType(),
                        parkingSlot.getStatus()
                );
        }

        BuildingZone zone = listZonesUseCase.getZoneById(parkingSlot.getZoneId());

        return new ParkingSlotResponse(
                parkingSlot.getId(),
                parkingSlot.getSlotCode(),
                parkingSlot.getFloor(),
                parkingSlot.getVehicleType(),
                parkingSlot.getStatus(),
                zone.getId(),
                zone.getZoneCode(),
                zone.getZoneName(),
                zone.getFloorId(),
                zone.getFloorCode(),
                zone.getFloorName()
        );
        }
}