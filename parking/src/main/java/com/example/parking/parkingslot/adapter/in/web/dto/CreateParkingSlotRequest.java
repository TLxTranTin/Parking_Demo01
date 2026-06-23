package com.example.parking.parkingslot.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateParkingSlotRequest {

    @NotBlank(message = "Slot code is required")
    private String slotCode;

    @NotBlank(message = "Floor is required")
    private String floor;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private Long zoneId;

    public CreateParkingSlotRequest() {
    }

    public String getSlotCode() { return slotCode; }

    public String getFloor() { return floor; }

    public VehicleType getVehicleType() { return vehicleType; }

    public Long getZoneId() { return zoneId; }
}