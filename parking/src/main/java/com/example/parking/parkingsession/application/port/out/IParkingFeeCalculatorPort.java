package com.example.parking.parkingsession.application.port.out;

import com.example.parking.pricing.domain.model.PricingResult;

public interface IParkingFeeCalculatorPort {

    PricingResult calculateFee(Long parkingSessionId);
}