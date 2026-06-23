package com.example.parking.parkingsession.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CheckOutResponse {

    private final Long sessionId;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final String slotCode;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final long durationHours;
    private final BigDecimal totalAmount;

    public CheckOutResponse(
            Long sessionId,
            String plateNumber,
            VehicleType vehicleType,
            String slotCode,
            LocalDateTime checkInTime,
            LocalDateTime checkOutTime,
            long durationHours,
            BigDecimal totalAmount
    ) {
        this.sessionId = sessionId;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.slotCode = slotCode;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.durationHours = durationHours;
        this.totalAmount = totalAmount;
    }

    public Long getSessionId() {
        return sessionId;
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

    public long getDurationHours() {
        return durationHours;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}