package com.example.parking.vehicle.application.port.out;

import com.example.parking.vehicle.domain.model.Vehicle;
import java.util.List;
import java.util.Optional;

public interface IVehicleRepositoryPort {

    Vehicle save(Vehicle vehicle);

    List<Vehicle> findAll();

    Optional<Vehicle> findById(Long id);

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);

    boolean existsById(Long id);

    void deleteById(Long id);
}