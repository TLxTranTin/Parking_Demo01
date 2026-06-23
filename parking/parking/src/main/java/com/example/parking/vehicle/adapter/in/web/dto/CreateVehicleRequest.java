package com.example.parking.vehicle.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateVehicleRequest {

    @NotBlank(message = "Plate number is required")
    @Size(max = 30, message = "Plate number must not exceed 30 characters")
    private String plateNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotBlank(message = "Owner name is required")
    @Size(max = 100, message = "Owner name must not exceed 100 characters")
    private String ownerName;

    public CreateVehicleRequest() {
    }

    public CreateVehicleRequest(String plateNumber, VehicleType type, String ownerName) {
        this.plateNumber = plateNumber;
        this.type = type;
        this.ownerName = ownerName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getType() {
        return type;
    }

    public String getOwnerName() {
        return ownerName;
    }
}