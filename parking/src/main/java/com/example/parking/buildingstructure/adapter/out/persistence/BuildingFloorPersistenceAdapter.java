package com.example.parking.buildingstructure.adapter.out.persistence;

import com.example.parking.buildingstructure.application.port.out.IBuildingFloorRepositoryPort;
import com.example.parking.buildingstructure.domain.model.BuildingFloor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BuildingFloorPersistenceAdapter implements IBuildingFloorRepositoryPort {

    private final ISpringDataBuildingFloorRepository springDataBuildingFloorRepository;

    public BuildingFloorPersistenceAdapter(
            ISpringDataBuildingFloorRepository springDataBuildingFloorRepository
    ) {
        this.springDataBuildingFloorRepository = springDataBuildingFloorRepository;
    }

    @Override
    public BuildingFloor save(BuildingFloor floor) {
        BuildingFloorEntity entity = toEntity(floor);
        BuildingFloorEntity savedEntity = springDataBuildingFloorRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<BuildingFloor> findById(Long id) {
        return springDataBuildingFloorRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByFloorCode(String floorCode) {
        return springDataBuildingFloorRepository.existsByFloorCode(floorCode);
    }

    @Override
    public List<BuildingFloor> findAllOrderByCreatedAtDesc() {
        return springDataBuildingFloorRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private BuildingFloor toDomain(BuildingFloorEntity entity) {
        return new BuildingFloor(
                entity.getId(),
                entity.getFloorCode(),
                entity.getFloorName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private BuildingFloorEntity toEntity(BuildingFloor floor) {
        return new BuildingFloorEntity(
                floor.getId(),
                floor.getFloorCode(),
                floor.getFloorName(),
                floor.getDescription(),
                floor.isActive(),
                floor.getCreatedAt(),
                floor.getUpdatedAt()
        );
    }
}