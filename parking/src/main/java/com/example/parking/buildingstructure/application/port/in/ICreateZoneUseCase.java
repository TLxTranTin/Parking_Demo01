package com.example.parking.buildingstructure.application.port.in;

import com.example.parking.buildingstructure.domain.model.BuildingZone;

public interface ICreateZoneUseCase {

    BuildingZone createZone(
            Long floorId,
            String zoneCode,
            String zoneName,
            String vehicleType,
            String description
    );
}