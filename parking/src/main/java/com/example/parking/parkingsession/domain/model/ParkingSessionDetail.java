package com.example.parking.parkingsession.domain.model;

import com.example.parking.vehicle.domain.model.VehicleType;

import java.time.LocalDateTime;

public class ParkingSessionDetail {

    private final Long id;
    private final Long vehicleId;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final Long parkingSlotId;
    private final String slotCode;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final ParkingSessionStatus status;

    public ParkingSessionDetail(
            Long id,
            Long vehicleId,
            String plateNumber,
            VehicleType vehicleType,
            Long parkingSlotId,
            String slotCode,
            LocalDateTime checkInTime,
            LocalDateTime checkOutTime,
            ParkingSessionStatus status
    ) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.parkingSlotId = parkingSlotId;
        this.slotCode = slotCode;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Long getParkingSlotId() {
        return parkingSlotId;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public ParkingSessionStatus getStatus() {
        return status;
    }
}