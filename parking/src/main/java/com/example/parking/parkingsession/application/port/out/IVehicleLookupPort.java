package com.example.parking.parkingsession.application.port.out;

import com.example.parking.vehicle.domain.model.Vehicle;

import java.util.Optional;

public interface IVehicleLookupPort {

    Optional<Vehicle> findByPlateNumber(String plateNumber);
}