package com.example.parking.parkingslot.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.parking.parkingslot.domain.model.SlotStatus;

import java.util.List;
import java.util.Optional;

public interface ISpringDataParkingSlotRepository extends JpaRepository<ParkingSlotEntity, Long> {

    List<ParkingSlotEntity> findByStatus(SlotStatus status);

    Optional<ParkingSlotEntity> findBySlotCode(String slotCode);

    boolean existsBySlotCode(String slotCode);
}