package com.example.parking.vehicle.adapter.out.persistence;

import com.example.parking.vehicle.domain.model.VehicleStatus;
import com.example.parking.vehicle.domain.model.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 30)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VehicleType type;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VehicleStatus status;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "description", length = 255)
    private String description;

    protected VehicleEntity() {
    }

    public VehicleEntity(
            Long id,
            String plateNumber,
            VehicleType type,
            String ownerName,
            Long ownerUserId,
            VehicleStatus status,
            String brand,
            String color,
            String description
    ) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.type = type;
        this.ownerName = ownerName;
        this.ownerUserId = ownerUserId;
        this.status = status == null ? VehicleStatus.APPROVED : status;
        this.brand = brand;
        this.color = color;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public VehicleType getType() {
        return type;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }
}