package com.example.parking.vehicle.application.port.in;

import com.example.parking.vehicle.domain.model.Vehicle;
import com.example.parking.vehicle.domain.model.VehicleType;

public interface IUpdateVehicleUseCase {

    Vehicle updateVehicle(Long id, String plateNumber, VehicleType type, String ownerName);
}