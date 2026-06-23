package com.example.parking.parkingslot.application.port.in;

import com.example.parking.parkingslot.domain.model.ParkingSlot;
import java.util.List;

public interface IGetParkingSlotUseCase {

    List<ParkingSlot> getAllParkingSlots();

    List<ParkingSlot> getAvailableParkingSlots();
}