package com.example.parking.invoice.adapter.in.web.dto;

import com.example.parking.invoice.domain.model.InvoiceStatus;
import com.example.parking.invoice.domain.model.InvoiceType;
import com.example.parking.vehicle.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {

    private final Long id;
    private final Long parkingSessionId;
    private final Long userId;
    private final String plateNumber;
    private final VehicleType vehicleType;
    private final BigDecimal amount;
    private final InvoiceStatus status;
    private final InvoiceType type;
    private final LocalDateTime issuedAt;
    private final LocalDateTime paidAt;

    public InvoiceResponse(
            Long id,
            Long parkingSessionId,
            Long userId,
            String plateNumber,
            VehicleType vehicleType,
            BigDecimal amount,
            InvoiceStatus status,
            InvoiceType type,
            LocalDateTime issuedAt,
            LocalDateTime paidAt
    ) {
        this.id = id;
        this.parkingSessionId = parkingSessionId;
        this.userId = userId;
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.issuedAt = issuedAt;
        this.paidAt = paidAt;
    }

    public Long getId() {
        return id;
    }

    public Long getParkingSessionId() {
        return parkingSessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public InvoiceType getType() {
        return type;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}