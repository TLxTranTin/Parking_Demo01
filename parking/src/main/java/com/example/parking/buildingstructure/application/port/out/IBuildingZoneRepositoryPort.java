package com.example.parking.buildingstructure.application.port.out;

import com.example.parking.buildingstructure.domain.model.BuildingZone;

import java.util.List;
import java.util.Optional;

public interface IBuildingZoneRepositoryPort {

    BuildingZone save(BuildingZone zone);

    Optional<BuildingZone> findById(Long id);

    boolean existsByFloorIdAndZoneCode(Long floorId, String zoneCode);

    List<BuildingZone> findAllOrderByCreatedAtDesc();
}