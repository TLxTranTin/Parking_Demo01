package com.example.parking.parkingsession.adapter.out.persistence;

import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISpringDataParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

    Optional<ParkingSessionEntity> findByVehicleIdAndStatus(
            Long vehicleId,
            ParkingSessionStatus status
    );

    boolean existsByVehicleIdAndStatus(
            Long vehicleId,
            ParkingSessionStatus status
    );
}