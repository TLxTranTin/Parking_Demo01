package com.example.parking.invoice.adapter.out.persistence;

import com.example.parking.invoice.domain.model.InvoiceStatus;
import com.example.parking.invoice.domain.model.InvoiceType;
import com.example.parking.vehicle.domain.model.VehicleType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parking_session_id", nullable = false, unique = true)
    private Long parkingSessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "plate_number", nullable = false, length = 30)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 30)
    private VehicleType vehicleType;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private InvoiceType type;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    protected InvoiceEntity() {
    }

    public InvoiceEntity(
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