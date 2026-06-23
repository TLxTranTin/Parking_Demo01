package com.example.parking.incident.application.port.in;

import com.example.parking.incident.domain.model.IncidentReport;

import java.time.LocalDate;
import java.util.List;

public interface IGetIncidentReportUseCase {

    List<IncidentReport> getMyReports(Long currentUserId);

    List<IncidentReport> getReports(
            String status,
            String type,
            String priority,
            Long userId,
            String plateNumber,
            LocalDate fromDate,
            LocalDate toDate
    );
}