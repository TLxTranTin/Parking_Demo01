package com.example.parking.parkingslot.adapter.in.web.dto;

import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.vehicle.domain.model.VehicleType;

public class ParkingSlotResponse {

    private final Long id;
    private final String slotCode;
    private final String floor;
    private final VehicleType vehicleType;
    private final SlotStatus status;

    public ParkingSlotResponse(
            Long id,
            String slotCode,
            String floor,
            VehicleType vehicleType,
            SlotStatus status
    ) {
        this.id = id;
        this.slotCode = slotCode;
        this.floor = floor;
        this.vehicleType = vehicleType;
        this.status = status;
    }

    public Long getId() {
        return id;
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

    public SlotStatus getStatus() {
        return status;
    }
}