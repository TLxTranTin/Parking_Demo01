package com.example.parking.pricing.adapter.out.vehicle;

import com.example.parking.pricing.application.port.out.IPricingVehicleLookupPort;
import com.example.parking.vehicle.application.port.out.IVehicleRepositoryPort;
import com.example.parking.vehicle.domain.model.Vehicle;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PricingVehicleLookupAdapter implements IPricingVehicleLookupPort {

    private final IVehicleRepositoryPort vehicleRepositoryPort;

    public PricingVehicleLookupAdapter(IVehicleRepositoryPort vehicleRepositoryPort) {
        this.vehicleRepositoryPort = vehicleRepositoryPort;
    }

    @Override
    public Optional<Vehicle> findById(Long vehicleId) {
        return vehicleRepositoryPort.findById(vehicleId);
    }
}