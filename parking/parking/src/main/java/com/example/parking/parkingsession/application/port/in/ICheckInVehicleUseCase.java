package com.example.parking.parkingsession.application.port.in;

import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.vehicle.domain.model.VehicleType;

public interface ICheckInVehicleUseCase {

    ParkingSession checkIn(String plateNumber, VehicleType vehicleType);
}