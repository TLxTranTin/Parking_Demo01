package com.example.parking.parkingslot.application.port.in;

import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.vehicle.domain.model.VehicleType;

public interface ICreateParkingSlotUseCase {

    ParkingSlot createParkingSlot(
        String slotCode,
        String floor,
        VehicleType vehicleType,
        Long zoneId
    );
}