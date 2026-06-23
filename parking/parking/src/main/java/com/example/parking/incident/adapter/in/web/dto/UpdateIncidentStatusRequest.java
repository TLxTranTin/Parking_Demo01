package com.example.parking.incident.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateIncidentStatusRequest {

    @NotBlank(message = "Incident status is required")
    private String status;

    @Size(max = 1000, message = "Staff note must not exceed 1000 characters")
    private String staffNote;

    public UpdateIncidentStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public String getStaffNote() {
        return staffNote;
    }
}