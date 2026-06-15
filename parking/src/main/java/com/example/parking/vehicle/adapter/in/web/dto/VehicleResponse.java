package com.example.parking.vehicle.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;

public class VehicleResponse {

    private final Long id;
    private final String plateNumber;
    private final VehicleType type;
    private final String ownerName;

    public VehicleResponse(Long id, String plateNumber, VehicleType type, String ownerName) {
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