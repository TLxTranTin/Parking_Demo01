package com.example.parking.vehicle.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleStatus;
import com.example.parking.vehicle.domain.model.VehicleType;

public class VehicleResponse {

    private final Long id;
    private final String plateNumber;
    private final VehicleType type;
    private final String ownerName;

    private final Long ownerUserId;
    private final VehicleStatus status;
    private final String brand;
    private final String color;
    private final String description;

    public VehicleResponse(
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
        this.status = status;
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