package com.example.parking.incident.adapter.in.web.dto;

import com.example.parking.incident.domain.model.IncidentPriority;
import com.example.parking.incident.domain.model.IncidentStatus;
import com.example.parking.incident.domain.model.IncidentType;

import java.time.LocalDateTime;

public class IncidentReportResponse {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String description;
    private final IncidentType type;
    private final IncidentPriority priority;
    private final IncidentStatus status;
    private final String plateNumber;
    private final String staffNote;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime resolvedAt;

    public IncidentReportResponse(
            Long id,
            Long userId,
            String title,
            String description,
            IncidentType type,
            IncidentPriority priority,
            IncidentStatus status,
            String plateNumber,
            String staffNote,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime resolvedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.plateNumber = plateNumber;
        this.staffNote = staffNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IncidentType getType() {
        return type;
    }

    public IncidentPriority getPriority() {
        return priority;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getStaffNote() {
        return staffNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}