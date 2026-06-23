package com.example.parking.incident.adapter.out.persistence;

import com.example.parking.incident.application.port.out.IIncidentReportRepositoryPort;
import com.example.parking.incident.domain.model.IncidentReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IncidentReportPersistenceAdapter implements IIncidentReportRepositoryPort {

    private final ISpringDataIncidentReportRepository springDataIncidentReportRepository;

    public IncidentReportPersistenceAdapter(
            ISpringDataIncidentReportRepository springDataIncidentReportRepository
    ) {
        this.springDataIncidentReportRepository = springDataIncidentReportRepository;
    }

    @Override
    public IncidentReport save(IncidentReport incidentReport) {
        IncidentReportEntity entity = toEntity(incidentReport);
        IncidentReportEntity savedEntity = springDataIncidentReportRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<IncidentReport> findById(Long id) {
        return springDataIncidentReportRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<IncidentReport> findAllOrderByCreatedAtDesc() {
        return springDataIncidentReportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<IncidentReport> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return springDataIncidentReportRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private IncidentReport toDomain(IncidentReportEntity entity) {
        return new IncidentReport(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getType(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getPlateNumber(),
                entity.getStaffNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getResolvedAt()
        );
    }

    private IncidentReportEntity toEntity(IncidentReport incidentReport) {
        return new IncidentReportEntity(
                incidentReport.getId(),
                incidentReport.getUserId(),
                incidentReport.getTitle(),
                incidentReport.getDescription(),
                incidentReport.getType(),
                incidentReport.getPriority(),
                incidentReport.getStatus(),
                incidentReport.getPlateNumber(),
                incidentReport.getStaffNote(),
                incidentReport.getCreatedAt(),
                incidentReport.getUpdatedAt(),
                incidentReport.getResolvedAt()
        );
    }
}