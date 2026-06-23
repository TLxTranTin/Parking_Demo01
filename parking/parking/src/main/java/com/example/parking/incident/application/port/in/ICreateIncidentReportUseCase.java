package com.example.parking.incident.application.port.in;

import com.example.parking.incident.domain.model.IncidentReport;

public interface ICreateIncidentReportUseCase {

    IncidentReport createMyReport(
            Long currentUserId,
            String title,
            String description,
            String type,
            String priority,
            String plateNumber
    );
}