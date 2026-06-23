package com.example.parking.buildingstructure.application.port.in;

import com.example.parking.buildingstructure.domain.model.BuildingFloor;

public interface ICreateFloorUseCase {

    BuildingFloor createFloor(
            String floorCode,
            String floorName,
            String description
    );
}