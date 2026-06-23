package com.example.parking.buildingstructure.application.port.in;

import com.example.parking.buildingstructure.domain.model.BuildingFloor;

public interface ISetFloorActiveUseCase {

    BuildingFloor setFloorActive(Long id, boolean active);
}