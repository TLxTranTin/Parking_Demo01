package com.example.parking.buildingstructure.application.port.out;

import com.example.parking.buildingstructure.domain.model.BuildingFloor;

import java.util.List;
import java.util.Optional;

public interface IBuildingFloorRepositoryPort {

    BuildingFloor save(BuildingFloor floor);

    Optional<BuildingFloor> findById(Long id);

    boolean existsByFloorCode(String floorCode);

    List<BuildingFloor> findAllOrderByCreatedAtDesc();
}