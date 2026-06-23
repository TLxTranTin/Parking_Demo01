package com.example.parking.parkingsession.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;
import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    private VehicleType vehicleType;

    public CheckInRequest() {
    }

    public CheckInRequest(String plateNumber, VehicleType vehicleType) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}