package com.example.parking.parkingslot.adapter.out.persistence;

import com.example.parking.parkingslot.application.port.out.IParkingSlotRepositoryPort;
import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParkingSlotPersistenceAdapter implements IParkingSlotRepositoryPort {

    private final ISpringDataParkingSlotRepository springDataParkingSlotRepository;

    public ParkingSlotPersistenceAdapter(ISpringDataParkingSlotRepository springDataParkingSlotRepository) {
        this.springDataParkingSlotRepository = springDataParkingSlotRepository;
    }

    @Override
    public ParkingSlot save(ParkingSlot parkingSlot) {
        ParkingSlotEntity entity = toEntity(parkingSlot);
        ParkingSlotEntity savedEntity = springDataParkingSlotRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<ParkingSlot> findAll() {
        return springDataParkingSlotRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ParkingSlot> findByStatus(SlotStatus status) {
        return springDataParkingSlotRepository.findByStatus(status)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ParkingSlot> findById(Long id) {
        return springDataParkingSlotRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ParkingSlot> findBySlotCode(String slotCode) {
        return springDataParkingSlotRepository.findBySlotCode(slotCode)
                .map(this::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return springDataParkingSlotRepository.existsById(id);
    }

    @Override
    public boolean existsBySlotCode(String slotCode) {
        return springDataParkingSlotRepository.existsBySlotCode(slotCode);
    }

    @Override
    public void deleteById(Long id) {
        springDataParkingSlotRepository.deleteById(id);
    }

    private ParkingSlot toDomain(ParkingSlotEntity entity) {
        return new ParkingSlot(
                entity.getId(),
                entity.getSlotCode(),
                entity.getFloor(),
                entity.getVehicleType(),
                entity.getStatus(),
                entity.getZoneId()
        );
    }

    private ParkingSlotEntity toEntity(ParkingSlot parkingSlot) {
        return new ParkingSlotEntity(
                parkingSlot.getId(),
                parkingSlot.getSlotCode(),
                parkingSlot.getFloor(),
                parkingSlot.getVehicleType(),
                parkingSlot.getStatus(),
                parkingSlot.getZoneId()
        );
    }
}