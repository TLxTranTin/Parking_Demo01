package com.example.parking.parkingsession.adapter.in.web;

import com.example.parking.parkingsession.adapter.in.web.dto.CheckInRequest;
import com.example.parking.parkingsession.adapter.in.web.dto.CheckOutRequest;
import com.example.parking.parkingsession.adapter.in.web.dto.ParkingSessionResponse;
import com.example.parking.parkingsession.application.port.in.ICheckInVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.ICheckOutVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.IGetParkingSessionUseCase;
import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.parking.parkingsession.adapter.in.web.dto.CheckOutResponse;
import com.example.parking.parkingsession.domain.model.CheckOutResult;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/parking-sessions")
public class ParkingSessionController {

    private final ICheckInVehicleUseCase checkInVehicleUseCase;
    private final ICheckOutVehicleUseCase checkOutVehicleUseCase;
    private final IGetParkingSessionUseCase getParkingSessionUseCase;

    public ParkingSessionController(
            ICheckInVehicleUseCase checkInVehicleUseCase,
            ICheckOutVehicleUseCase checkOutVehicleUseCase,
            IGetParkingSessionUseCase getParkingSessionUseCase
    ) {
        this.checkInVehicleUseCase = checkInVehicleUseCase;
        this.checkOutVehicleUseCase = checkOutVehicleUseCase;
        this.getParkingSessionUseCase = getParkingSessionUseCase;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<ParkingSessionResponse>> checkIn(
            @Valid @RequestBody CheckInRequest request
    ) {
        ParkingSession parkingSession = checkInVehicleUseCase.checkIn(
                request.getPlateNumber()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Check-in successfully", toResponse(parkingSession))
        );
    }

    // @PostMapping("/check-out")
    // public ResponseEntity<ApiResponse<ParkingSessionResponse>> checkOut(
    //         @Valid @RequestBody CheckOutRequest request
    // ) {
    //     ParkingSession parkingSession = checkOutVehicleUseCase.checkOut(
    //             request.getPlateNumber()
    //     );

    //     return ResponseEntity.ok(
    //             ApiResponse.success("Check-out successfully", toResponse(parkingSession))
    //     );
    // }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingSessionResponse>>> getAllParkingSessions() {
        List<ParkingSessionResponse> sessions = getParkingSessionUseCase.getAllParkingSessions()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get parking sessions successfully", sessions)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ParkingSessionResponse>> getActiveSessionByPlateNumber(
            @RequestParam @NotBlank(message = "Plate number is required") String plateNumber
    ) {
        ParkingSession parkingSession = getParkingSessionUseCase
                .getActiveSessionByPlateNumber(plateNumber);

        return ResponseEntity.ok(
                ApiResponse.success("Get active parking session successfully", toResponse(parkingSession))
        );
    }

    private ParkingSessionResponse toResponse(ParkingSession parkingSession) {
        return new ParkingSessionResponse(
                parkingSession.getId(),
                parkingSession.getVehicleId(),
                parkingSession.getParkingSlotId(),
                parkingSession.getCheckInTime(),
                parkingSession.getCheckOutTime(),
                parkingSession.getStatus()
        );
    }
    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<CheckOutResponse>> checkOut(
            @Valid @RequestBody CheckOutRequest request
    ) {
        CheckOutResult checkOutResult = checkOutVehicleUseCase.checkOut(
                request.getPlateNumber()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Check-out successfully", toCheckOutResponse(checkOutResult))
        );
    }

    private CheckOutResponse toCheckOutResponse(CheckOutResult checkOutResult) {
        return new CheckOutResponse(
                checkOutResult.getSessionId(),
                checkOutResult.getPlateNumber(),
                checkOutResult.getVehicleType(),
                checkOutResult.getSlotCode(),
                checkOutResult.getCheckInTime(),
                checkOutResult.getCheckOutTime(),
                checkOutResult.getDurationHours(),
                checkOutResult.getTotalAmount()
        );
    }
}