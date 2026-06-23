package com.example.parking.buildingstructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "building_floors")
public class BuildingFloorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "floor_code", nullable = false, unique = true, length = 30)
    private String floorCode;

    @Column(name = "floor_name", nullable = false, length = 100)
    private String floorName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected BuildingFloorEntity() {
    }

    public BuildingFloorEntity(
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