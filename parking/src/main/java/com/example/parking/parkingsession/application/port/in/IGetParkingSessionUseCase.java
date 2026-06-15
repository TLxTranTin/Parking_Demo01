package com.example.parking.parkingsession.application.port.in;

import com.example.parking.parkingsession.domain.model.ParkingSession;

import java.util.List;

public interface IGetParkingSessionUseCase {

    List<ParkingSession> getAllParkingSessions();

    ParkingSession getActiveSessionByPlateNumber(String plateNumber);
}