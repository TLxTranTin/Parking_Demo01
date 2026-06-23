package com.example.parking.parkingsession.adapter.out.persistence;

import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISpringDataParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

    List<ParkingSessionEntity> findAllByOrderByCheckInTimeDesc();

    List<ParkingSessionEntity> findByStatus(ParkingSessionStatus status);

    Optional<ParkingSessionEntity> findByVehicleIdAndStatus(
            Long vehicleId,
            ParkingSessionStatus status
    );

    boolean existsByVehicleIdAndStatus(
            Long vehicleId,
            ParkingSessionStatus status
    );
}