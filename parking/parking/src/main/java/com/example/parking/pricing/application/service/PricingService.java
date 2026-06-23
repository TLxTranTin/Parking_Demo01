package com.example.parking.pricing.application.service;

import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.pricing.application.port.in.ICalculateParkingFeeUseCase;
import com.example.parking.pricing.application.port.out.IPricingParkingSessionLookupPort;
import com.example.parking.pricing.application.port.out.IPricingVehicleLookupPort;
import com.example.parking.pricing.domain.model.PricingResult;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.domain.model.Vehicle;
import com.example.parking.vehicle.domain.model.VehicleType;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService implements ICalculateParkingFeeUseCase {

    private static final BigDecimal MOTORBIKE_HOURLY_RATE = BigDecimal.valueOf(5000);
    private static final BigDecimal CAR_HOURLY_RATE = BigDecimal.valueOf(20000);

    private final IPricingParkingSessionLookupPort parkingSessionLookupPort;
    private final IPricingVehicleLookupPort vehicleLookupPort;

    public PricingService(
            IPricingParkingSessionLookupPort parkingSessionLookupPort,
            IPricingVehicleLookupPort vehicleLookupPort
    ) {
        this.parkingSessionLookupPort = parkingSessionLookupPort;
        this.vehicleLookupPort = vehicleLookupPort;
    }

    @Override
    public PricingResult calculateFee(Long parkingSessionId) {
        if (parkingSessionId == null) {
            throw new BusinessException("Parking session id is required");
        }

        ParkingSession parkingSession = parkingSessionLookupPort.findById(parkingSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found"));

        if (parkingSession.getCheckOutTime() == null) {
            throw new BusinessException("Parking session is still active. Please check out before calculating fee");
        }

        Vehicle vehicle = vehicleLookupPort.findById(parkingSession.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        long durationHours = calculateDurationHours(
                parkingSession.getCheckInTime(),
                parkingSession.getCheckOutTime()
        );

        BigDecimal hourlyRate = getHourlyRate(vehicle.getType());
        BigDecimal totalAmount = hourlyRate.multiply(BigDecimal.valueOf(durationHours));

        return new PricingResult(
                parkingSession.getId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                parkingSession.getCheckInTime(),
                parkingSession.getCheckOutTime(),
                durationHours,
                totalAmount
        );
    }

    private long calculateDurationHours(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        if (checkInTime == null) {
            throw new BusinessException("Check-in time is required");
        }

        if (checkOutTime.isBefore(checkInTime)) {
            throw new BusinessException("Check-out time must be after check-in time");
        }

        long seconds = Duration.between(checkInTime, checkOutTime).getSeconds();

        long roundedHours = (seconds + 3599) / 3600;

        return Math.max(roundedHours, 1);
    }

    private BigDecimal getHourlyRate(VehicleType vehicleType) {
        if (vehicleType == VehicleType.MOTORBIKE) {
            return MOTORBIKE_HOURLY_RATE;
        }

        if (vehicleType == VehicleType.CAR) {
            return CAR_HOURLY_RATE;
        }

        throw new BusinessException("Unsupported vehicle type");
    }
}