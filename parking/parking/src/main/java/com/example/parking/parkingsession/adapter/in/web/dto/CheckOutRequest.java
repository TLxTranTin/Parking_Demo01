package com.example.parking.parkingsession.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckOutRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    public CheckOutRequest() {
    }

    public CheckOutRequest(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }
}