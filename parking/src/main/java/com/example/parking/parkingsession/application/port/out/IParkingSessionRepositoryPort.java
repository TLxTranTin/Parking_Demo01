package com.example.parking.parkingsession.application.port.out;

import com.example.parking.parkingsession.domain.model.ParkingSession;

import java.util.List;
import java.util.Optional;

public interface IParkingSessionRepositoryPort {

    ParkingSession save(ParkingSession parkingSession);

    List<ParkingSession> findAll();

    Optional<ParkingSession> findById(Long id);

    Optional<ParkingSession> findActiveByVehicleId(Long vehicleId);

    boolean existsActiveByVehicleId(Long vehicleId);
}