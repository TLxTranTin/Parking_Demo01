package com.example.parking.buildingstructure.application.port.in;

import com.example.parking.buildingstructure.domain.model.BuildingZone;

public interface ISetZoneActiveUseCase {

    BuildingZone setZoneActive(Long id, boolean active);
}