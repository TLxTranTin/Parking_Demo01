package com.example.parking.incident.adapter.in.web;

import com.example.parking.incident.adapter.in.web.dto.CreateIncidentReportRequest;
import com.example.parking.incident.adapter.in.web.dto.IncidentReportResponse;
import com.example.parking.incident.adapter.in.web.dto.UpdateIncidentStatusRequest;
import com.example.parking.incident.application.port.in.ICreateIncidentReportUseCase;
import com.example.parking.incident.application.port.in.IGetIncidentReportUseCase;
import com.example.parking.incident.application.port.in.IUpdateIncidentStatusUseCase;
import com.example.parking.incident.domain.model.IncidentReport;
import com.example.parking.shared.response.ApiResponse;
import com.example.parking.shared.security.CurrentUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentReportController {

    private final ICreateIncidentReportUseCase createIncidentReportUseCase;
    private final IGetIncidentReportUseCase getIncidentReportUseCase;
    private final IUpdateIncidentStatusUseCase updateIncidentStatusUseCase;

    public IncidentReportController(
            ICreateIncidentReportUseCase createIncidentReportUseCase,
            IGetIncidentReportUseCase getIncidentReportUseCase,
            IUpdateIncidentStatusUseCase updateIncidentStatusUseCase
    ) {
        this.createIncidentReportUseCase = createIncidentReportUseCase;
        this.getIncidentReportUseCase = getIncidentReportUseCase;
        this.updateIncidentStatusUseCase = updateIncidentStatusUseCase;
    }

    @PostMapping("/my")
    public ResponseEntity<ApiResponse<IncidentReportResponse>> createMyReport(
            @Valid @RequestBody CreateIncidentReportRequest request,
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return unauthorized();
        }

        IncidentReport report = createIncidentReportUseCase.createMyReport(
                currentUser.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getType(),
                request.getPriority(),
                request.getPlateNumber()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Create incident report successfully", toResponse(report))
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<IncidentReportResponse>>> getMyReports(
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return unauthorized();
        }

        List<IncidentReportResponse> reports = getIncidentReportUseCase.getMyReports(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get my incident reports successfully", reports)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IncidentReportResponse>>> getReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<IncidentReportResponse> reports = getIncidentReportUseCase.getReports(
                        status,
                        type,
                        priority,
                        userId,
                        plateNumber,
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get incident reports successfully", reports)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<IncidentReportResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentStatusRequest request
    ) {
        IncidentReport report = updateIncidentStatusUseCase.updateStatus(
                id,
                request.getStatus(),
                request.getStaffNote()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Update incident status successfully", toResponse(report))
        );
    }

    private CurrentUserPrincipal getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CurrentUserPrincipal currentUser)) {
            return null;
        }

        return currentUser;
    }

    private <T> ResponseEntity<ApiResponse<T>> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Unauthorized"));
    }

    private IncidentReportResponse toResponse(IncidentReport report) {
        return new IncidentReportResponse(
                report.getId(),
                report.getUserId(),
                report.getTitle(),
                report.getDescription(),
                report.getType(),
                report.getPriority(),
                report.getStatus(),
                report.getPlateNumber(),
                report.getStaffNote(),
                report.getCreatedAt(),
                report.getUpdatedAt(),
                report.getResolvedAt()
        );
    }
}