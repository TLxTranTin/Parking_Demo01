package com.example.parking.pricing.application.port.in;

import com.example.parking.pricing.domain.model.PricingResult;

public interface ICalculateParkingFeeUseCase {

    PricingResult calculateFee(Long parkingSessionId);
}