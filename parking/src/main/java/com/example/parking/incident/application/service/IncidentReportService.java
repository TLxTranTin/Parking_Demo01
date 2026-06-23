package com.example.parking.incident.application.service;

import com.example.parking.incident.application.port.in.ICreateIncidentReportUseCase;
import com.example.parking.incident.application.port.in.IGetIncidentReportUseCase;
import com.example.parking.incident.application.port.in.IUpdateIncidentStatusUseCase;
import com.example.parking.incident.application.port.out.IIncidentReportRepositoryPort;
import com.example.parking.incident.domain.model.IncidentPriority;
import com.example.parking.incident.domain.model.IncidentReport;
import com.example.parking.incident.domain.model.IncidentStatus;
import com.example.parking.incident.domain.model.IncidentType;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentReportService implements
        ICreateIncidentReportUseCase,
        IGetIncidentReportUseCase,
        IUpdateIncidentStatusUseCase {

    private final IIncidentReportRepositoryPort incidentReportRepositoryPort;

    public IncidentReportService(IIncidentReportRepositoryPort incidentReportRepositoryPort) {
        this.incidentReportRepositoryPort = incidentReportRepositoryPort;
    }

    @Override
    public IncidentReport createMyReport(
            Long currentUserId,
            String title,
            String description,
            String type,
            String priority,
            String plateNumber
    ) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        String normalizedTitle = normalizeRequiredText(title, "Title is required");
        String normalizedDescription = normalizeRequiredText(description, "Description is required");
        IncidentType incidentType = parseType(type);
        IncidentPriority incidentPriority = parsePriorityOrDefault(priority);
        String normalizedPlateNumber = normalizeOptionalPlateNumber(plateNumber);

        LocalDateTime now = LocalDateTime.now();

        IncidentReport incidentReport = new IncidentReport(
                null,
                currentUserId,
                normalizedTitle,
                normalizedDescription,
                incidentType,
                incidentPriority,
                IncidentStatus.OPEN,
                normalizedPlateNumber,
                null,
                now,
                now,
                null
        );

        return incidentReportRepositoryPort.save(incidentReport);
    }

    @Override
    public List<IncidentReport> getMyReports(Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        return incidentReportRepositoryPort.findByUserIdOrderByCreatedAtDesc(currentUserId);
    }

    @Override
    public List<IncidentReport> getReports(
            String status,
            String type,
            String priority,
            Long userId,
            String plateNumber,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        IncidentStatus parsedStatus = parseOptionalStatus(status);
        IncidentType parsedType = parseOptionalType(type);
        IncidentPriority parsedPriority = parseOptionalPriority(priority);
        String normalizedPlateNumber = normalizeOptionalPlateNumber(plateNumber);
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTimeExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();

        return incidentReportRepositoryPort.findAllOrderByCreatedAtDesc()
                .stream()
                .filter(report -> matchesStatus(report, parsedStatus))
                .filter(report -> matchesType(report, parsedType))
                .filter(report -> matchesPriority(report, parsedPriority))
                .filter(report -> matchesUserId(report, userId))
                .filter(report -> matchesPlateNumber(report, normalizedPlateNumber))
                .filter(report -> matchesDateRange(report, fromDateTime, toDateTimeExclusive))
                .toList();
    }

    @Override
    public IncidentReport updateStatus(
            Long incidentId,
            String status,
            String staffNote
    ) {
        IncidentReport currentReport = incidentReportRepositoryPort.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident report not found"));

        IncidentStatus newStatus = parseStatus(status);
        String normalizedStaffNote = normalizeOptionalText(staffNote);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime resolvedAt = null;

        if (newStatus == IncidentStatus.RESOLVED || newStatus == IncidentStatus.REJECTED) {
            resolvedAt = now;
        }

        IncidentReport updatedReport = new IncidentReport(
                currentReport.getId(),
                currentReport.getUserId(),
                currentReport.getTitle(),
                currentReport.getDescription(),
                currentReport.getType(),
                currentReport.getPriority(),
                newStatus,
                currentReport.getPlateNumber(),
                normalizedStaffNote,
                currentReport.getCreatedAt(),
                now,
                resolvedAt
        );

        return incidentReportRepositoryPort.save(updatedReport);
    }

    private String normalizeRequiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(message);
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeOptionalPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            return null;
        }

        return plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
    }

    private IncidentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new BusinessException("Incident status is required");
        }

        try {
            return IncidentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid incident status");
        }
    }

    private IncidentStatus parseOptionalStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        return parseStatus(status);
    }

    private IncidentType parseType(String type) {
        if (type == null || type.isBlank()) {
            throw new BusinessException("Incident type is required");
        }

        try {
            return IncidentType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid incident type");
        }
    }

    private IncidentType parseOptionalType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        return parseType(type);
    }

    private IncidentPriority parsePriorityOrDefault(String priority) {
        if (priority == null || priority.isBlank()) {
            return IncidentPriority.LOW;
        }

        return parsePriority(priority);
    }

    private IncidentPriority parsePriority(String priority) {
        try {
            return IncidentPriority.valueOf(priority.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid incident priority");
        }
    }

    private IncidentPriority parseOptionalPriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return null;
        }

        return parsePriority(priority);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException("fromDate must be before or equal to toDate");
        }
    }

    private boolean matchesStatus(IncidentReport report, IncidentStatus status) {
        if (status == null) {
            return true;
        }

        return report.getStatus() == status;
    }

    private boolean matchesType(IncidentReport report, IncidentType type) {
        if (type == null) {
            return true;
        }

        return report.getType() == type;
    }

    private boolean matchesPriority(IncidentReport report, IncidentPriority priority) {
        if (priority == null) {
            return true;
        }

        return report.getPriority() == priority;
    }

    private boolean matchesUserId(IncidentReport report, Long userId) {
        if (userId == null) {
            return true;
        }

        return report.getUserId() != null && report.getUserId().equals(userId);
    }

    private boolean matchesPlateNumber(IncidentReport report, String plateNumber) {
        if (plateNumber == null) {
            return true;
        }

        if (report.getPlateNumber() == null) {
            return false;
        }

        return report.getPlateNumber()
                .trim()
                .toUpperCase()
                .replaceAll("\\s+", "")
                .contains(plateNumber);
    }

    private boolean matchesDateRange(
            IncidentReport report,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTimeExclusive
    ) {
        LocalDateTime createdAt = report.getCreatedAt();

        if (createdAt == null) {
            return false;
        }

        if (fromDateTime != null && createdAt.isBefore(fromDateTime)) {
            return false;
        }

        if (toDateTimeExclusive != null && !createdAt.isBefore(toDateTimeExclusive)) {
            return false;
        }

        return true;
    }
}