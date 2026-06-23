package com.example.parking.parkingslot.domain.model;

import com.example.parking.vehicle.domain.model.VehicleType;

public class ParkingSlot {

    private final Long id;
    private final String slotCode;
    private final String floor;
    private final VehicleType vehicleType;
    private final SlotStatus status;
    private final Long zoneId;

    public ParkingSlot(
            Long id,
            String slotCode,
            String floor,
            VehicleType vehicleType,
            SlotStatus status,
            Long zoneId
    ) {
        this.id = id;
        this.slotCode = slotCode;
        this.floor = floor;
        this.vehicleType = vehicleType;
        this.status = status;
        this.zoneId = zoneId;
    }

        public ParkingSlot(
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
        this.zoneId = null;
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

    public Long getZoneId(){
        return zoneId;
    }
}