package com.example.parking.parkingsession.adapter.out.parkingslot;

import com.example.parking.parkingsession.application.port.out.IParkingSlotManagementPort;
import com.example.parking.parkingslot.application.port.out.IParkingSlotRepositoryPort;
import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.vehicle.domain.model.VehicleType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ParkingSlotManagementAdapter implements IParkingSlotManagementPort {

    private final IParkingSlotRepositoryPort parkingSlotRepositoryPort;

    public ParkingSlotManagementAdapter(IParkingSlotRepositoryPort parkingSlotRepositoryPort) {
        this.parkingSlotRepositoryPort = parkingSlotRepositoryPort;
    }

    @Override
    public Optional<ParkingSlot> findById(Long parkingSlotId) {
        return parkingSlotRepositoryPort.findById(parkingSlotId);
    }

    @Override
    public Optional<ParkingSlot> findAvailableSlotByVehicleType(VehicleType vehicleType) {
        return parkingSlotRepositoryPort.findByStatus(SlotStatus.AVAILABLE)
                .stream()
                .filter(slot -> slot.getVehicleType() == vehicleType)
                .findFirst();
    }

    @Override
    public ParkingSlot updateStatus(Long parkingSlotId, SlotStatus status) {
        ParkingSlot currentSlot = parkingSlotRepositoryPort.findById(parkingSlotId)
                .orElseThrow(() -> new IllegalStateException("Parking slot not found"));

        ParkingSlot updatedSlot = new ParkingSlot(
                currentSlot.getId(),
                currentSlot.getSlotCode(),
                currentSlot.getFloor(),
                currentSlot.getVehicleType(),
                status
        );

        return parkingSlotRepositoryPort.save(updatedSlot);
    }
}