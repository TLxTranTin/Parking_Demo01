package com.example.parking.vehicle.application.port.in;

import com.example.parking.vehicle.domain.model.Vehicle;

import java.util.List;

public interface IGetVehicleUseCase {

    List<Vehicle> getAllVehicles();

    Vehicle getVehicleByPlateNumber(String plateNumber);

    List<Vehicle> getVehiclesForCurrentUser(Long currentUserId, String currentRole);

    Vehicle searchVehicleByPlateNumberForCurrentUser(
            String plateNumber,
            Long currentUserId,
            String currentRole
    );
}