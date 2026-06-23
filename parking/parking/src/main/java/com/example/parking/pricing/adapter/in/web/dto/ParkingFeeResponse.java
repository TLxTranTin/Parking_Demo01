package com.example.parking.pricing.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.parking.vehicle.domain.model.VehicleType;

public class ParkingFeeResponse {

    private final Long sessionId;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final long durationHours;
    private final BigDecimal totalAmount;

    public ParkingFeeResponse(
            Long sessionId,
            String plateNumber,
            VehicleType vehicleType,
            LocalDateTime checkInTime,
            LocalDateTime checkOutTime,
            long durationHours,
            BigDecimal totalAmount
    ) {
        this.sessionId = sessionId;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
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