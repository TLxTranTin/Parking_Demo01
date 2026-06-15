package com.example.parking.vehicle.application.service;

import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.application.port.in.ICreateVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IDeleteVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IGetVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IUpdateVehicleUseCase;
import com.example.parking.vehicle.application.port.out.IVehicleRepositoryPort;
import com.example.parking.vehicle.domain.model.Vehicle;
import com.example.parking.vehicle.domain.model.VehicleType;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService implements
        ICreateVehicleUseCase,
        IGetVehicleUseCase,
        IUpdateVehicleUseCase,
        IDeleteVehicleUseCase {

    private final IVehicleRepositoryPort vehicleRepositoryPort;

    public VehicleService(IVehicleRepositoryPort vehicleRepositoryPort) {
        this.vehicleRepositoryPort = vehicleRepositoryPort;
    }

    @Override
    public Vehicle createVehicle(String plateNumber, VehicleType type, String ownerName) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);
        String normalizedOwnerName = normalizeOwnerName(ownerName);
        validateVehicleType(type);

        if (vehicleRepositoryPort.existsByPlateNumber(normalizedPlateNumber)) {
            throw new BusinessException("Plate number already exists");
        }

        Vehicle vehicle = new Vehicle(
                null,
                normalizedPlateNumber,
                type,
                normalizedOwnerName
        );

        return vehicleRepositoryPort.save(vehicle);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepositoryPort.findAll();
    }

    @Override
    public Vehicle getVehicleByPlateNumber(String plateNumber) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        return vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    @Override
    public Vehicle updateVehicle(Long id, String plateNumber, VehicleType type, String ownerName) {
        Vehicle currentVehicle = vehicleRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        String normalizedPlateNumber = normalizePlateNumber(plateNumber);
        String normalizedOwnerName = normalizeOwnerName(ownerName);
        validateVehicleType(type);

        vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber)
                .ifPresent(existingVehicle -> {
                    boolean isAnotherVehicle = !existingVehicle.getId().equals(currentVehicle.getId());

                    if (isAnotherVehicle) {
                        throw new BusinessException("Plate number already exists");
                    }
                });

        Vehicle updatedVehicle = new Vehicle(
                currentVehicle.getId(),
                normalizedPlateNumber,
                type,
                normalizedOwnerName
        );

        return vehicleRepositoryPort.save(updatedVehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        if (!vehicleRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found");
        }

        vehicleRepositoryPort.deleteById(id);
    }

    private String normalizePlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }

        return plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
    }
    private String normalizeOwnerName(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new BusinessException("Owner name is required");
        }

        return ownerName.trim();
    }

    private void validateVehicleType(VehicleType type) {
        if (type == null) {
            throw new BusinessException("Vehicle type is required");
        }
    }
}