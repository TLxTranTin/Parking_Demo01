package com.example.parking.parkingsession.application.port.in;

import com.example.parking.parkingsession.domain.model.ParkingSession;

public interface ICheckInVehicleUseCase {

    ParkingSession checkIn(String plateNumber);
}