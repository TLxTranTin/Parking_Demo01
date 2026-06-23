package com.example.parking.parkingsession.adapter.in.web.dto;

import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import com.example.parking.vehicle.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkingSessionHistoryResponse {

    private final Long id;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final String slotCode;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final Long durationHours;
    private final BigDecimal totalAmount;
    private final ParkingSessionStatus status;

    public ParkingSessionHistoryResponse(
            Long id,
            String plateNumber,
            VehicleType vehicleType,
            String slotCode,
            LocalDateTime checkInTime,
            LocalDateTime checkOutTime,
            Long durationHours,
            BigDecimal totalAmount,
            ParkingSessionStatus status
    ) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.slotCode = slotCode;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.durationHours = durationHours;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
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

    public Long getDurationHours() {
        return durationHours;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public ParkingSessionStatus getStatus() {
        return status;
    }
}