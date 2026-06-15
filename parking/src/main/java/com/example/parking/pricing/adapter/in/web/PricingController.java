package com.example.parking.pricing.adapter.in.web;

import com.example.parking.pricing.adapter.in.web.dto.CalculateParkingFeeRequest;
import com.example.parking.pricing.adapter.in.web.dto.ParkingFeeResponse;
import com.example.parking.pricing.application.port.in.ICalculateParkingFeeUseCase;
import com.example.parking.pricing.domain.model.PricingResult;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final ICalculateParkingFeeUseCase calculateParkingFeeUseCase;

    public PricingController(ICalculateParkingFeeUseCase calculateParkingFeeUseCase) {
        this.calculateParkingFeeUseCase = calculateParkingFeeUseCase;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<ParkingFeeResponse>> calculateFee(
            @Valid @RequestBody CalculateParkingFeeRequest request
    ) {
        PricingResult pricingResult = calculateParkingFeeUseCase.calculateFee(
                request.getParkingSessionId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Calculate parking fee successfully", toResponse(pricingResult))
        );
    }

    private ParkingFeeResponse toResponse(PricingResult pricingResult) {
        return new ParkingFeeResponse(
                pricingResult.getSessionId(),
                pricingResult.getPlateNumber(),
                pricingResult.getVehicleType(),
                pricingResult.getCheckInTime(),
                pricingResult.getCheckOutTime(),
                pricingResult.getDurationHours(),
                pricingResult.getTotalAmount()
        );
    }
}