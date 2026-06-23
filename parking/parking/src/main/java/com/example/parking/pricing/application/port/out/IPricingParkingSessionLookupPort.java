package com.example.parking.pricing.application.port.out;

import com.example.parking.parkingsession.domain.model.ParkingSession;

import java.util.Optional;

public interface IPricingParkingSessionLookupPort {

    Optional<ParkingSession> findById(Long parkingSessionId);
}