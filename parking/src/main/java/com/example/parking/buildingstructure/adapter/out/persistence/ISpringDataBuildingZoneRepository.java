package com.example.parking.buildingstructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISpringDataBuildingZoneRepository extends JpaRepository<BuildingZoneEntity, Long> {

    boolean existsByFloorIdAndZoneCode(Long floorId, String zoneCode);

    List<BuildingZoneEntity> findAllByOrderByCreatedAtDesc();
}