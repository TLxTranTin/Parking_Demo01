package com.example.parking.parkingslot.adapter.out.persistence;

import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.vehicle.domain.model.VehicleType;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_slots")
public class ParkingSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_code", nullable = false, unique = true, length = 50)
    private String slotCode;

    @Column(nullable = false, length = 50)
    private String floor;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 30)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SlotStatus status;

    @Column(name = "zone_id")
    private Long zoneId;

    protected ParkingSlotEntity() {
    }

    public ParkingSlotEntity(
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

    public Long getId() {
        return id;
    }

    public Long getZoneId() {
        return zoneId;
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