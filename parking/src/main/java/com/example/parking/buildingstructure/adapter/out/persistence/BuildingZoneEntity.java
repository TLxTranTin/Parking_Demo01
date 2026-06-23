package com.example.parking.buildingstructure.adapter.out.persistence;

import com.example.parking.vehicle.domain.model.VehicleType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "building_zones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_building_zones_floor_zone_code",
                        columnNames = {"floor_id", "zone_code"}
                )
        }
)
public class BuildingZoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "floor_id", nullable = false)
    private Long floorId;

    @Column(name = "zone_code", nullable = false, length = 30)
    private String zoneCode;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", length = 30)
    private VehicleType vehicleType;

    @Column(name = "description", length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected BuildingZoneEntity() {
    }

    public BuildingZoneEntity(
            Long id,
            Long floorId,
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

    public String getZoneCode() { return zoneCode; }

    public String getZoneName() { return zoneName; }

    public VehicleType getVehicleType() { return vehicleType; }

    public String getDescription() { return description; }

    public boolean isActive() { return active; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}