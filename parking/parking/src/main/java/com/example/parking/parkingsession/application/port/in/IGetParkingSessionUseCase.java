package com.example.parking.parkingsession.application.port.in;

import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionDetail;

import java.util.List;

public interface IGetParkingSessionUseCase {

    List<ParkingSession> getAllParkingSessions();

    List<ParkingSession> getActiveParkingSessions();

    ParkingSession getActiveSessionByPlateNumber(String plateNumber);

    ParkingSessionDetail getParkingSessionDetailById(Long parkingSessionId);

    List<ParkingSessionDetail> getAllParkingSessionDetails();

    List<ParkingSessionDetail> getActiveParkingSessionDetails();

    ParkingSessionDetail getActiveSessionDetailByPlateNumber(String plateNumber);
}