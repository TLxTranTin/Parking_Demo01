package com.example.parking.parkingsession.adapter.out.pricing;

import com.example.parking.parkingsession.application.port.out.IParkingFeeCalculatorPort;
import com.example.parking.pricing.application.port.in.ICalculateParkingFeeUseCase;
import com.example.parking.pricing.domain.model.PricingResult;
import org.springframework.stereotype.Component;

@Component
public class ParkingFeeCalculatorAdapter implements IParkingFeeCalculatorPort {

    private final ICalculateParkingFeeUseCase calculateParkingFeeUseCase;

    public ParkingFeeCalculatorAdapter(ICalculateParkingFeeUseCase calculateParkingFeeUseCase) {
        this.calculateParkingFeeUseCase = calculateParkingFeeUseCase;
    }

    @Override
    public PricingResult calculateFee(Long parkingSessionId) {
        return calculateParkingFeeUseCase.calculateFee(parkingSessionId);
    }
}