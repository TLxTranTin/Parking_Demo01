package com.example.parking.parkingsession.application.port.out;

import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.vehicle.domain.model.VehicleType;

import java.util.Optional;

public interface IParkingSlotManagementPort {

    Optional<ParkingSlot> findById(Long parkingSlotId);

    Optional<ParkingSlot> findAvailableSlotByVehicleType(VehicleType vehicleType);

    ParkingSlot updateStatus(Long parkingSlotId, SlotStatus status);
}