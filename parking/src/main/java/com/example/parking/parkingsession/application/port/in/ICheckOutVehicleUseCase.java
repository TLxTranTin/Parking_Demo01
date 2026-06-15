package com.example.parking.parkingsession.application.port.in;

import com.example.parking.parkingsession.domain.model.CheckOutResult;

public interface ICheckOutVehicleUseCase {

    CheckOutResult checkOut(String plateNumber);
}