package com.example.parking.pricing.adapter.out.parkingsession;

import com.example.parking.parkingsession.application.port.out.IParkingSessionRepositoryPort;
import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.pricing.application.port.out.IPricingParkingSessionLookupPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PricingParkingSessionLookupAdapter implements IPricingParkingSessionLookupPort {

    private final IParkingSessionRepositoryPort parkingSessionRepositoryPort;

    public PricingParkingSessionLookupAdapter(IParkingSessionRepositoryPort parkingSessionRepositoryPort) {
        this.parkingSessionRepositoryPort = parkingSessionRepositoryPort;
    }

    @Override
    public Optional<ParkingSession> findById(Long parkingSessionId) {
        return parkingSessionRepositoryPort.findById(parkingSessionId);
    }
}