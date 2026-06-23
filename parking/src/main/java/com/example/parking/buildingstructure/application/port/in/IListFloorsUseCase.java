package com.example.parking.buildingstructure.application.port.in;

import com.example.parking.buildingstructure.domain.model.BuildingFloor;

import java.util.List;

public interface IListFloorsUseCase {

    List<BuildingFloor> getFloors(Boolean active);
}