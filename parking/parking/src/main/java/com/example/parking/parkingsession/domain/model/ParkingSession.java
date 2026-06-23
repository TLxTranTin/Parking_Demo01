package com.example.parking.parkingsession.domain.model;

import java.time.LocalDateTime;

public class ParkingSession {

    private final Long id;
    private final Long vehicleId;
    private final Long parkingSlotId;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final ParkingSessionStatus status;

    public ParkingSession(
            Long id,
            Long vehicleId,
            Long parkingSlotId,
            LocalDateTime checkInTime,
            LocalDateTime checkOutTime,
            ParkingSessionStatus status
    ) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.parkingSlotId = parkingSlotId;
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

    public Long getParkingSlotId() {
        return parkingSlotId;
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