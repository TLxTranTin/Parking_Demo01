package com.example.parking.incident.application.port.out;

import com.example.parking.incident.domain.model.IncidentReport;

import java.util.List;
import java.util.Optional;

public interface IIncidentReportRepositoryPort {

    IncidentReport save(IncidentReport incidentReport);

    Optional<IncidentReport> findById(Long id);

    List<IncidentReport> findAllOrderByCreatedAtDesc();

    List<IncidentReport> findByUserIdOrderByCreatedAtDesc(Long userId);
}