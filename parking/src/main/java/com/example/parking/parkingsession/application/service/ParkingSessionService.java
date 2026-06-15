package com.example.parking.parkingsession.application.service;

import com.example.parking.parkingsession.application.port.in.ICheckInVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.ICheckOutVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.IGetParkingSessionUseCase;
import com.example.parking.parkingsession.application.port.out.IParkingFeeCalculatorPort;
import com.example.parking.parkingsession.application.port.out.IParkingSessionRepositoryPort;
import com.example.parking.parkingsession.application.port.out.IParkingSlotManagementPort;
import com.example.parking.parkingsession.application.port.out.IVehicleLookupPort;
import com.example.parking.parkingsession.domain.model.CheckOutResult;
import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.pricing.domain.model.PricingResult;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.domain.model.Vehicle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingSessionService implements
        ICheckInVehicleUseCase,
        ICheckOutVehicleUseCase,
        IGetParkingSessionUseCase {

    private final IParkingSessionRepositoryPort parkingSessionRepositoryPort;
    private final IVehicleLookupPort vehicleLookupPort;
    private final IParkingSlotManagementPort parkingSlotManagementPort;
    private final IParkingFeeCalculatorPort parkingFeeCalculatorPort;

    public ParkingSessionService(
            IParkingSessionRepositoryPort parkingSessionRepositoryPort,
            IVehicleLookupPort vehicleLookupPort,
            IParkingSlotManagementPort parkingSlotManagementPort,
            IParkingFeeCalculatorPort parkingFeeCalculatorPort
    ) {
        this.parkingSessionRepositoryPort = parkingSessionRepositoryPort;
        this.vehicleLookupPort = vehicleLookupPort;
        this.parkingSlotManagementPort = parkingSlotManagementPort;
        this.parkingFeeCalculatorPort = parkingFeeCalculatorPort;
    }

    @Override
    @Transactional
    public ParkingSession checkIn(String plateNumber) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        Vehicle vehicle = vehicleLookupPort.findByPlateNumber(normalizedPlateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (parkingSessionRepositoryPort.existsActiveByVehicleId(vehicle.getId())) {
            throw new BusinessException("Vehicle already has an active parking session");
        }

        ParkingSlot availableSlot = parkingSlotManagementPort
                .findAvailableSlotByVehicleType(vehicle.getType())
                .orElseThrow(() -> new BusinessException("No available parking slot for this vehicle type"));

        ParkingSession newSession = new ParkingSession(
                null,
                vehicle.getId(),
                availableSlot.getId(),
                LocalDateTime.now(),
                null,
                ParkingSessionStatus.ACTIVE
        );

        ParkingSession savedSession = parkingSessionRepositoryPort.save(newSession);

        parkingSlotManagementPort.updateStatus(
                availableSlot.getId(),
                SlotStatus.OCCUPIED
        );

        return savedSession;
    }

    @Override
    @Transactional
    public CheckOutResult checkOut(String plateNumber) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        Vehicle vehicle = vehicleLookupPort.findByPlateNumber(normalizedPlateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        ParkingSession activeSession = parkingSessionRepositoryPort
                .findActiveByVehicleId(vehicle.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active parking session not found"));

        ParkingSession completedSession = new ParkingSession(
                activeSession.getId(),
                activeSession.getVehicleId(),
                activeSession.getParkingSlotId(),
                activeSession.getCheckInTime(),
                LocalDateTime.now(),
                ParkingSessionStatus.COMPLETED
        );

        ParkingSession savedSession = parkingSessionRepositoryPort.save(completedSession);

        ParkingSlot updatedSlot = parkingSlotManagementPort.updateStatus(
                activeSession.getParkingSlotId(),
                SlotStatus.AVAILABLE
        );

        PricingResult pricingResult = parkingFeeCalculatorPort.calculateFee(savedSession.getId());

        return new CheckOutResult(
                savedSession.getId(),
                pricingResult.getPlateNumber(),
                pricingResult.getVehicleType(),
                updatedSlot.getSlotCode(),
                pricingResult.getCheckInTime(),
                pricingResult.getCheckOutTime(),
                pricingResult.getDurationHours(),
                pricingResult.getTotalAmount()
        );
    }

    @Override
    public List<ParkingSession> getAllParkingSessions() {
        return parkingSessionRepositoryPort.findAll();
    }

    @Override
    public ParkingSession getActiveSessionByPlateNumber(String plateNumber) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        Vehicle vehicle = vehicleLookupPort.findByPlateNumber(normalizedPlateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        return parkingSessionRepositoryPort.findActiveByVehicleId(vehicle.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active parking session not found"));
    }

    private String normalizePlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }

        return plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
    }
}