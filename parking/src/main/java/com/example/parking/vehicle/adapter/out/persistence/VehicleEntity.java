package com.example.parking.vehicle.adapter.out.persistence;

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

    protected VehicleEntity() {
    }

    public VehicleEntity(Long id, String plateNumber, VehicleType type, String ownerName) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.type = type;
        this.ownerName = ownerName;
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
}