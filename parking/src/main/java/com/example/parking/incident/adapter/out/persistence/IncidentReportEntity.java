package com.example.parking.incident.adapter.out.persistence;

import com.example.parking.incident.domain.model.IncidentPriority;
import com.example.parking.incident.domain.model.IncidentStatus;
import com.example.parking.incident.domain.model.IncidentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_reports")
public class IncidentReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private IncidentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 30)
    private IncidentPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private IncidentStatus status;

    @Column(name = "plate_number", length = 30)
    private String plateNumber;

    @Column(name = "staff_note", length = 1000)
    private String staffNote;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    protected IncidentReportEntity() {
    }

    public IncidentReportEntity(
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