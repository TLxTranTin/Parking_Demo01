package com.example.parking.parkingslot.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateParkingSlotRequest {

    @NotBlank(message = "Slot code is required")
    @Size(max = 50, message = "Slot code must not exceed 50 characters")
    private String slotCode;

    @NotBlank(message = "Floor is required")
    @Size(max = 50, message = "Floor must not exceed 50 characters")
    private String floor;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    public CreateParkingSlotRequest() {
    }

    public CreateParkingSlotRequest(String slotCode, String floor, VehicleType vehicleType) {
        this.slotCode = slotCode;
        this.floor = floor;
        this.vehicleType = vehicleType;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public String getFloor() {
        return floor;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}