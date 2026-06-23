package com.example.parking.buildingstructure.application.port.in;

import com.example.parking.buildingstructure.domain.model.BuildingZone;

import java.util.List;

public interface IListZonesUseCase {

    List<BuildingZone> getZones(
            Long floorId,
            Boolean active,
            String vehicleType
    );

    BuildingZone getZoneById(Long id);
}