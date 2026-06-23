package com.example.parking.vehicle.application.port.in;

import com.example.parking.vehicle.domain.model.Vehicle;
import com.example.parking.vehicle.domain.model.VehicleType;

public interface IRegisterVehicleUseCase {

    Vehicle registerVehicle(
            Long currentUserId,
            String username,
            String plateNumber,
            VehicleType type,
            String brand,
            String color,
            String description
    );
}