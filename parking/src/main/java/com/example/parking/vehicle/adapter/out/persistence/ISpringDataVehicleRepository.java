package com.example.parking.vehicle.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISpringDataVehicleRepository extends JpaRepository<VehicleEntity, Long> {

    Optional<VehicleEntity> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);
}