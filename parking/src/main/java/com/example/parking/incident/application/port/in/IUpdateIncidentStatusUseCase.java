package com.example.parking.incident.application.port.in;

import com.example.parking.incident.domain.model.IncidentReport;

public interface IUpdateIncidentStatusUseCase {

    IncidentReport updateStatus(
            Long incidentId,
            String status,
            String staffNote
    );
}