package com.example.parking.parkingslot.application.port.in;

import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;

public interface IUpdateParkingSlotStatusUseCase {

    ParkingSlot updateStatus(Long id, SlotStatus status);
}