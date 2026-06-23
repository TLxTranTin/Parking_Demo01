package com.example.parking.buildingstructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateZoneRequest {

    @NotNull(message = "Floor id is required")
    private Long floorId;

    @NotBlank(message = "Zone code is required")
    @Size(max = 30, message = "Zone code must not exceed 30 characters")
    private String zoneCode;

    @NotBlank(message = "Zone name is required")
    @Size(max = 100, message = "Zone name must not exceed 100 characters")
    private String zoneName;

    private String vehicleType;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    public CreateZoneRequest() {
    }

    public Long getFloorId() { return floorId; }

    public String getZoneCode() { return zoneCode; }

    public String getZoneName() { return zoneName; }

    public String getVehicleType() { return vehicleType; }

    public String getDescription() { return description; }
}