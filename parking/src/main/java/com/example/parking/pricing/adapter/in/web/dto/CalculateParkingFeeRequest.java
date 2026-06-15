package com.example.parking.pricing.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public class CalculateParkingFeeRequest {

    @NotNull(message = "Parking session id is required")
    private Long parkingSessionId;

    public CalculateParkingFeeRequest() {
    }

    public CalculateParkingFeeRequest(Long parkingSessionId) {
        this.parkingSessionId = parkingSessionId;
    }

    public Long getParkingSessionId() {
        return parkingSessionId;
    }
}