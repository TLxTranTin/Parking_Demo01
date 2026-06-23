package com.example.parking.buildingstructure.adapter.in.web.dto;

import com.example.parking.vehicle.domain.model.VehicleType;

import java.time.LocalDateTime;

public class ZoneResponse {

    private final Long id;
    private final Long floorId;
    private final String floorCode;
    private final String floorName;
    private final String zoneCode;
    private final String zoneName;
    private final VehicleType vehicleType;
    private final String description;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ZoneResponse(
            Long id,
            Long floorId,
            String floorCode,
            String floorName,
            String zoneCode,
            String zoneName,
            VehicleType vehicleType,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.floorId = floorId;
        this.floorCode = floorCode;
        this.floorName = floorName;
        this.zoneCode = zoneCode;
        this.zoneName = zoneName;
        this.vehicleType = vehicleType;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }

    public Long getFloorId() { return floorId; }

    public String getFloorCode() { return floorCode; }

    public String getFloorName() { return floorName; }

    public String getZoneCode() { return zoneCode; }

    public String getZoneName() { return zoneName; }

    public VehicleType getVehicleType() { return vehicleType; }

    public String getDescription() { return description; }

    public boolean isActive() { return active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}