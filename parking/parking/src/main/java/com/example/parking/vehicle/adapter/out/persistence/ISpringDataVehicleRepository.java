package com.example.parking.vehicle.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISpringDataVehicleRepository extends JpaRepository<VehicleEntity, Long> {

    Optional<VehicleEntity> findByPlateNumber(String plateNumber);

    List<VehicleEntity> findByOwnerUserId(Long ownerUserId);

    boolean existsByPlateNumber(String plateNumber);
}