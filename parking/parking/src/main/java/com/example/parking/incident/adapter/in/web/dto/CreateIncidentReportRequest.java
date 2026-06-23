package com.example.parking.incident.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateIncidentReportRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Incident type is required")
    private String type;

    private String priority;

    @Size(max = 30, message = "Plate number must not exceed 30 characters")
    private String plateNumber;

    public CreateIncidentReportRequest() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getPriority() {
        return priority;
    }

    public String getPlateNumber() {
        return plateNumber;
    }
}