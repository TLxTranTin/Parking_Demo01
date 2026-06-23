package com.example.parking.parkingslot.application.service;

import com.example.parking.buildingstructure.application.port.in.IListZonesUseCase;
import com.example.parking.buildingstructure.domain.model.BuildingZone;
import com.example.parking.parkingslot.application.port.in.ICreateParkingSlotUseCase;
import com.example.parking.parkingslot.application.port.in.IDeleteParkingSlotUseCase;
import com.example.parking.parkingslot.application.port.in.IGetParkingSlotUseCase;
import com.example.parking.parkingslot.application.port.in.IUpdateParkingSlotStatusUseCase;
import com.example.parking.parkingslot.application.port.out.IParkingSlotRepositoryPort;
import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.domain.model.VehicleType;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSlotService implements
        ICreateParkingSlotUseCase,
        IGetParkingSlotUseCase,
        IUpdateParkingSlotStatusUseCase,
        IDeleteParkingSlotUseCase {
    private final IListZonesUseCase listZonesUseCase;
    private final IParkingSlotRepositoryPort parkingSlotRepositoryPort;

    public ParkingSlotService(IParkingSlotRepositoryPort parkingSlotRepositoryPort , IListZonesUseCase listZonesUseCase) {
        this.parkingSlotRepositoryPort = parkingSlotRepositoryPort;
        this.listZonesUseCase = listZonesUseCase;
    }

    @Override
    public ParkingSlot createParkingSlot(
            String slotCode,
            String floor,
            VehicleType vehicleType,
            Long zoneId
    ) {
        String normalizedSlotCode = normalizeSlotCode(slotCode);
        String normalizedFloor = normalizeFloor(floor);

        if (vehicleType == null) {
            throw new BusinessException("Vehicle type is required");
        }

        if (parkingSlotRepositoryPort.existsBySlotCode(normalizedSlotCode)) {
            throw new BusinessException("Slot code already exists");
        }

        String floorToUse = normalizedFloor;

        if (zoneId != null) {
            BuildingZone zone = listZonesUseCase.getZoneById(zoneId);

            if (!zone.isActive()) {
                throw new BusinessException("Building zone is inactive");
            }

            if (zone.getVehicleType() != null && zone.getVehicleType() != vehicleType) {
                throw new BusinessException("Slot vehicle type does not match zone vehicle type");
            }

            if (zone.getFloorCode() != null && !zone.getFloorCode().isBlank()) {
                floorToUse = zone.getFloorCode();
            }
        }

        ParkingSlot parkingSlot = new ParkingSlot(
                null,
                normalizedSlotCode,
                floorToUse,
                vehicleType,
                SlotStatus.AVAILABLE,
                zoneId
        );

        return parkingSlotRepositoryPort.save(parkingSlot);
    }

    @Override
    public List<ParkingSlot> getAllParkingSlots() {
        return parkingSlotRepositoryPort.findAll();
    }

    @Override
    public List<ParkingSlot> getAvailableParkingSlots() {
        return parkingSlotRepositoryPort.findByStatus(SlotStatus.AVAILABLE);
    }

    @Override
    public ParkingSlot updateStatus(Long id, SlotStatus status) {
        ParkingSlot currentParkingSlot = parkingSlotRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found"));

        SlotStatus validStatus = validateSlotStatus(status);

        ParkingSlot updatedParkingSlot = new ParkingSlot(
                currentParkingSlot.getId(),
                currentParkingSlot.getSlotCode(),
                currentParkingSlot.getFloor(),
                currentParkingSlot.getVehicleType(),
                validStatus
        );

        return parkingSlotRepositoryPort.save(updatedParkingSlot);
    }

    @Override
    public void deleteParkingSlot(Long id) {
        ParkingSlot parkingSlot = parkingSlotRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found"));

        if (parkingSlot.getStatus() == SlotStatus.OCCUPIED) {
            throw new BusinessException("Cannot delete occupied parking slot");
        }

        parkingSlotRepositoryPort.deleteById(id);
    }

    private String normalizeSlotCode(String slotCode) {
        if (slotCode == null || slotCode.isBlank()) {
            throw new BusinessException("Slot code is required");
        }

        return slotCode.trim().toUpperCase().replaceAll("\\s+", "");
    }

    private String normalizeFloor(String floor) {
        if (floor == null || floor.isBlank()) {
            throw new BusinessException("Floor is required");
        }

        return floor.trim().toUpperCase();
    }
    private VehicleType validateVehicleType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new BusinessException("Vehicle type is required");
        }

        return vehicleType;
    }
    private SlotStatus validateSlotStatus(SlotStatus status) {
        if (status == null) {
            throw new BusinessException("Status is required");
        }

        return status;
    }
}