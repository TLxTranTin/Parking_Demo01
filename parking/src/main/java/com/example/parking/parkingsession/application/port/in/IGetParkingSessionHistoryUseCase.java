package com.example.parking.parkingsession.application.port.in;

import com.example.parking.parkingsession.domain.model.ParkingSessionHistoryItem;

import java.time.LocalDate;
import java.util.List;

public interface IGetParkingSessionHistoryUseCase {

    List<ParkingSessionHistoryItem> getHistory(
            String plateNumber,
            String status,
            LocalDate fromDate,
            LocalDate toDate
    );
}