package com.example.parking.buildingstructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public class FloorResponse {

    private final Long id;
    private final String floorCode;
    private final String floorName;
    private final String description;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public FloorResponse(
            Long id,
            String floorCode,
            String floorName,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.floorCode = floorCode;
        this.floorName = floorName;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }

    public String getFloorCode() { return floorCode; }

    public String getFloorName() { return floorName; }

    public String getDescription() { return description; }

    public boolean isActive() { return active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}