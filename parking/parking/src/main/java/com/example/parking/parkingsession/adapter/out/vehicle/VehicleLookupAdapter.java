package com.example.parking.parkingsession.adapter.out.vehicle;

import com.example.parking.parkingsession.application.port.out.IVehicleLookupPort;
import com.example.parking.vehicle.application.port.out.IVehicleRepositoryPort;
import com.example.parking.vehicle.domain.model.Vehicle;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VehicleLookupAdapter implements IVehicleLookupPort {

    private final IVehicleRepositoryPort vehicleRepositoryPort;

    public VehicleLookupAdapter(IVehicleRepositoryPort vehicleRepositoryPort) {
        this.vehicleRepositoryPort = vehicleRepositoryPort;
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return vehicleRepositoryPort.findByPlateNumber(plateNumber);
    }
}