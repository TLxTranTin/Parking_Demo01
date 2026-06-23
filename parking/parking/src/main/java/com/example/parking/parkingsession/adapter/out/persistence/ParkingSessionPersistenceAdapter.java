package com.example.parking.parkingsession.adapter.out.persistence;

import com.example.parking.parkingsession.application.port.out.IParkingSessionRepositoryPort;
import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParkingSessionPersistenceAdapter implements IParkingSessionRepositoryPort {

    private final ISpringDataParkingSessionRepository springDataParkingSessionRepository;

    public ParkingSessionPersistenceAdapter(
            ISpringDataParkingSessionRepository springDataParkingSessionRepository
    ) {
        this.springDataParkingSessionRepository = springDataParkingSessionRepository;
    }

    @Override
    public ParkingSession save(ParkingSession parkingSession) {
        ParkingSessionEntity entity = toEntity(parkingSession);
        ParkingSessionEntity savedEntity = springDataParkingSessionRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<ParkingSession> findAll() {
        return springDataParkingSessionRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ParkingSession> findAllOrderByCheckInTimeDesc() {
        return springDataParkingSessionRepository.findAllByOrderByCheckInTimeDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ParkingSession> findActiveByVehicleId(Long vehicleId) {
        return springDataParkingSessionRepository
                .findByVehicleIdAndStatus(vehicleId, ParkingSessionStatus.ACTIVE)
                .map(this::toDomain);
    }

    @Override
    public boolean existsActiveByVehicleId(Long vehicleId) {
        return springDataParkingSessionRepository
                .existsByVehicleIdAndStatus(vehicleId, ParkingSessionStatus.ACTIVE);
    }

    @Override
    public Optional<ParkingSession> findById(Long id) {
        return springDataParkingSessionRepository.findById(id)
                .map(this::toDomain);
    }
    private ParkingSession toDomain(ParkingSessionEntity entity) {
        return new ParkingSession(
                entity.getId(),
                entity.getVehicleId(),
                entity.getParkingSlotId(),
                entity.getCheckInTime(),
                entity.getCheckOutTime(),
                entity.getStatus()
        );
    }
    @Override
    public List<ParkingSession> findByStatus(ParkingSessionStatus status) {
        return springDataParkingSessionRepository.findByStatus(status)
                .stream()
                .map(this::toDomain)
                .toList();
    }


    private ParkingSessionEntity toEntity(ParkingSession parkingSession) {
        return new ParkingSessionEntity(
                parkingSession.getId(),
                parkingSession.getVehicleId(),
                parkingSession.getParkingSlotId(),
                parkingSession.getCheckInTime(),
                parkingSession.getCheckOutTime(),
                parkingSession.getStatus()
        );
    }
}