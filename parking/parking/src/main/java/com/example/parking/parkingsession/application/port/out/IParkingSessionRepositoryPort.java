package com.example.parking.parkingsession.application.port.out;

import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;

import java.util.List;
import java.util.Optional;

public interface IParkingSessionRepositoryPort {

    ParkingSession save(ParkingSession parkingSession);

    List<ParkingSession> findAll();

    List<ParkingSession> findAllOrderByCheckInTimeDesc();

    List<ParkingSession> findByStatus(ParkingSessionStatus status);

    Optional<ParkingSession> findById(Long id);

    Optional<ParkingSession> findActiveByVehicleId(Long vehicleId);

    boolean existsActiveByVehicleId(Long vehicleId);
}