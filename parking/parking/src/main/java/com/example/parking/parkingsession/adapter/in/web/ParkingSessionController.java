package com.example.parking.parkingsession.adapter.in.web;

import com.example.parking.parkingsession.adapter.in.web.dto.CheckInRequest;
import com.example.parking.parkingsession.adapter.in.web.dto.CheckOutRequest;
import com.example.parking.parkingsession.adapter.in.web.dto.CheckOutResponse;
import com.example.parking.parkingsession.adapter.in.web.dto.ParkingSessionResponse;
import com.example.parking.parkingsession.application.port.in.ICheckInVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.ICheckOutVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.IGetParkingSessionUseCase;
import com.example.parking.parkingsession.domain.model.CheckOutResult;
import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionDetail;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.parking.parkingsession.adapter.in.web.dto.ParkingSessionHistoryResponse;
import com.example.parking.parkingsession.application.port.in.IGetParkingSessionHistoryUseCase;
import com.example.parking.parkingsession.domain.model.ParkingSessionHistoryItem;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/parking-sessions")
public class ParkingSessionController {

    private final IGetParkingSessionHistoryUseCase getParkingSessionHistoryUseCase;
    private final ICheckInVehicleUseCase checkInVehicleUseCase;
    private final ICheckOutVehicleUseCase checkOutVehicleUseCase;
    private final IGetParkingSessionUseCase getParkingSessionUseCase;

    public ParkingSessionController(
            ICheckInVehicleUseCase checkInVehicleUseCase,
            ICheckOutVehicleUseCase checkOutVehicleUseCase,
            IGetParkingSessionUseCase getParkingSessionUseCase,
            IGetParkingSessionHistoryUseCase getParkingSessionHistoryUseCase
    ) {
        this.checkInVehicleUseCase = checkInVehicleUseCase;
        this.checkOutVehicleUseCase = checkOutVehicleUseCase;
        this.getParkingSessionUseCase = getParkingSessionUseCase;
        this.getParkingSessionHistoryUseCase = getParkingSessionHistoryUseCase;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<ParkingSessionResponse>> checkIn(
            @Valid @RequestBody CheckInRequest request
    ) {
        ParkingSession parkingSession = checkInVehicleUseCase.checkIn(
                request.getPlateNumber(),
                request.getVehicleType()
        );

        ParkingSessionDetail detail = getParkingSessionUseCase.getParkingSessionDetailById(
                parkingSession.getId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Check-in successfully", toResponse(detail))
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingSessionResponse>>> getAllParkingSessions() {
        List<ParkingSessionResponse> sessions = getParkingSessionUseCase.getAllParkingSessionDetails()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get parking sessions successfully", sessions)
        );
    }
    @GetMapping("/history")
        public ResponseEntity<ApiResponse<List<ParkingSessionHistoryResponse>>> getHistory(
                @RequestParam(required = false) String plateNumber,
                @RequestParam(required = false) String status,
                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
        ) {
        List<ParkingSessionHistoryResponse> history = getParkingSessionHistoryUseCase.getHistory(
                        plateNumber,
                        status,
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::toHistoryResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get parking session history successfully", history)
        );
        }
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ParkingSessionResponse>>> getActiveParkingSessions() {
        List<ParkingSessionResponse> sessions = getParkingSessionUseCase.getActiveParkingSessionDetails()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get active parking sessions successfully", sessions)
        );
    }

    @GetMapping("/active/by-plate")
    public ResponseEntity<ApiResponse<ParkingSessionResponse>> getActiveSessionByPlateNumber(
            @RequestParam @NotBlank(message = "Plate number is required") String plateNumber
    ) {
        ParkingSessionDetail detail = getParkingSessionUseCase
                .getActiveSessionDetailByPlateNumber(plateNumber);

        return ResponseEntity.ok(
                ApiResponse.success("Get active parking session successfully", toResponse(detail))
        );
    }

    private ParkingSessionResponse toResponse(ParkingSessionDetail detail) {
        return new ParkingSessionResponse(
                detail.getId(),
                detail.getVehicleId(),
                detail.getPlateNumber(),
                detail.getVehicleType(),
                detail.getParkingSlotId(),
                detail.getSlotCode(),
                detail.getCheckInTime(),
                detail.getCheckOutTime(),
                detail.getStatus()
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
    private ParkingSessionHistoryResponse toHistoryResponse(ParkingSessionHistoryItem item) {
        return new ParkingSessionHistoryResponse(
                item.getId(),
                item.getPlateNumber(),
                item.getVehicleType(),
                item.getSlotCode(),
                item.getCheckInTime(),
                item.getCheckOutTime(),
                item.getDurationHours(),
                item.getTotalAmount(),
                item.getStatus()
        );
        }
}