package com.example.parking.parkingsession.adapter.out.persistence;

import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_sessions")
public class ParkingSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "parking_slot_id", nullable = false)
    private Long parkingSlotId;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ParkingSessionStatus status;

    protected ParkingSessionEntity() {
    }

    public ParkingSessionEntity(
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