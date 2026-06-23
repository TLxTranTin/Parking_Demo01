package com.example.parking.buildingstructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateFloorRequest {

    @NotBlank(message = "Floor code is required")
    @Size(max = 30, message = "Floor code must not exceed 30 characters")
    private String floorCode;

    @NotBlank(message = "Floor name is required")
    @Size(max = 100, message = "Floor name must not exceed 100 characters")
    private String floorName;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    public CreateFloorRequest() {
    }

    public String getFloorCode() { return floorCode; }

    public String getFloorName() { return floorName; }

    public String getDescription() { return description; }
}