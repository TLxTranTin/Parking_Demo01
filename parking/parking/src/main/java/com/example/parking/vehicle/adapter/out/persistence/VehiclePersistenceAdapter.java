package com.example.parking.vehicle.adapter.out.persistence;

import com.example.parking.vehicle.application.port.out.IVehicleRepositoryPort;
import com.example.parking.vehicle.domain.model.Vehicle;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehiclePersistenceAdapter implements IVehicleRepositoryPort {

    private final ISpringDataVehicleRepository springDataVehicleRepository;

    public VehiclePersistenceAdapter(ISpringDataVehicleRepository springDataVehicleRepository) {
        this.springDataVehicleRepository = springDataVehicleRepository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = toEntity(vehicle);
        VehicleEntity savedEntity = springDataVehicleRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Vehicle> findAll() {
        return springDataVehicleRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return springDataVehicleRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return springDataVehicleRepository.findByPlateNumber(plateNumber)
                .map(this::toDomain);
    }

    @Override
    public List<Vehicle> findByOwnerUserId(Long ownerUserId) {
        return springDataVehicleRepository.findByOwnerUserId(ownerUserId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByPlateNumber(String plateNumber) {
        return springDataVehicleRepository.existsByPlateNumber(plateNumber);
    }

    @Override
    public boolean existsById(Long id) {
        return springDataVehicleRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        springDataVehicleRepository.deleteById(id);
    }

    private Vehicle toDomain(VehicleEntity entity) {
        return new Vehicle(
                entity.getId(),
                entity.getPlateNumber(),
                entity.getType(),
                entity.getOwnerName(),
                entity.getOwnerUserId(),
                entity.getStatus(),
                entity.getBrand(),
                entity.getColor(),
                entity.getDescription()
        );
    }

    private VehicleEntity toEntity(Vehicle vehicle) {
        return new VehicleEntity(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                vehicle.getOwnerName(),
                vehicle.getOwnerUserId(),
                vehicle.getStatus(),
                vehicle.getBrand(),
                vehicle.getColor(),
                vehicle.getDescription()
        );
    }
}