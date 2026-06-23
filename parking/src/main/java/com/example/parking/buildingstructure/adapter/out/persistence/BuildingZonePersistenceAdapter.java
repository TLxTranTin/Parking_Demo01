package com.example.parking.buildingstructure.adapter.out.persistence;

import com.example.parking.buildingstructure.application.port.out.IBuildingZoneRepositoryPort;
import com.example.parking.buildingstructure.domain.model.BuildingZone;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BuildingZonePersistenceAdapter implements IBuildingZoneRepositoryPort {

    private final ISpringDataBuildingZoneRepository springDataBuildingZoneRepository;

    public BuildingZonePersistenceAdapter(
            ISpringDataBuildingZoneRepository springDataBuildingZoneRepository
    ) {
        this.springDataBuildingZoneRepository = springDataBuildingZoneRepository;
    }

    @Override
    public BuildingZone save(BuildingZone zone) {
        BuildingZoneEntity entity = toEntity(zone);
        BuildingZoneEntity savedEntity = springDataBuildingZoneRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<BuildingZone> findById(Long id) {
        return springDataBuildingZoneRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByFloorIdAndZoneCode(Long floorId, String zoneCode) {
        return springDataBuildingZoneRepository.existsByFloorIdAndZoneCode(floorId, zoneCode);
    }

    @Override
    public List<BuildingZone> findAllOrderByCreatedAtDesc() {
        return springDataBuildingZoneRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private BuildingZone toDomain(BuildingZoneEntity entity) {
        return new BuildingZone(
                entity.getId(),
                entity.getFloorId(),
                null,
                null,
                entity.getZoneCode(),
                entity.getZoneName(),
                entity.getVehicleType(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private BuildingZoneEntity toEntity(BuildingZone zone) {
        return new BuildingZoneEntity(
                zone.getId(),
                zone.getFloorId(),
                zone.getZoneCode(),
                zone.getZoneName(),
                zone.getVehicleType(),
                zone.getDescription(),
                zone.isActive(),
                zone.getCreatedAt(),
                zone.getUpdatedAt()
        );
    }
}