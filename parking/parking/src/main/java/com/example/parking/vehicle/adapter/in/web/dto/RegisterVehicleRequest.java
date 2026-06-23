package com.example.parking.vehicle.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterVehicleRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    public RegisterVehicleRequest() {
    }

    public RegisterVehicleRequest(
            String plateNumber,
            VehicleType type,
            String brand,
            String color,
            String description
    ) {
        this.plateNumber = plateNumber;
        this.type = type;
        this.brand = brand;
        this.color = color;
        this.description = description;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }
}