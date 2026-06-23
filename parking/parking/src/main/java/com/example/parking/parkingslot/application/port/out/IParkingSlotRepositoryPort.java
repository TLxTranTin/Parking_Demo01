package com.example.parking.parkingslot.application.port.out;

import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;

import java.util.List;
import java.util.Optional;

public interface IParkingSlotRepositoryPort {

    ParkingSlot save(ParkingSlot parkingSlot);

    List<ParkingSlot> findAll();

    List<ParkingSlot> findByStatus(SlotStatus status);

    Optional<ParkingSlot> findById(Long id);

    Optional<ParkingSlot> findBySlotCode(String slotCode);

    boolean existsById(Long id);

    boolean existsBySlotCode(String slotCode);

    void deleteById(Long id);
}