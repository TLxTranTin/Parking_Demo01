package com.example.parking.pricing.application.port.out;

import com.example.parking.vehicle.domain.model.Vehicle;

import java.util.Optional;

public interface IPricingVehicleLookupPort {

    Optional<Vehicle> findById(Long vehicleId);
}