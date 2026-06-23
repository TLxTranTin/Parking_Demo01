package com.example.parking.vehicle.application.port.in;

import com.example.parking.vehicle.domain.model.Vehicle;

public interface IUnblockVehicleUseCase {

    Vehicle unblockVehicle(Long vehicleId);
}