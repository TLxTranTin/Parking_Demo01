package com.example.parking.parkingsession.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    public CheckInRequest() {
    }

    public CheckInRequest(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }
}