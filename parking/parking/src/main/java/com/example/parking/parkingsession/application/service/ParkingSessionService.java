package com.example.parking.parkingsession.application.service;

import com.example.parking.invoice.application.port.in.ICreateInvoiceUseCase;
import com.example.parking.parkingsession.application.port.in.ICheckInVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.ICheckOutVehicleUseCase;
import com.example.parking.parkingsession.application.port.in.IGetParkingSessionUseCase;
import com.example.parking.parkingsession.application.port.out.IParkingFeeCalculatorPort;
import com.example.parking.parkingsession.application.port.out.IParkingSessionRepositoryPort;
import com.example.parking.parkingsession.application.port.out.IParkingSlotManagementPort;
import com.example.parking.parkingsession.domain.model.CheckOutResult;
import com.example.parking.parkingsession.domain.model.ParkingSession;
import com.example.parking.parkingsession.domain.model.ParkingSessionDetail;
import com.example.parking.parkingsession.domain.model.ParkingSessionHistoryItem;
import com.example.parking.parkingsession.domain.model.ParkingSessionStatus;
import com.example.parking.parkingslot.domain.model.ParkingSlot;
import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.pricing.domain.model.PricingResult;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.application.port.out.IVehicleRepositoryPort;
import com.example.parking.vehicle.domain.model.Vehicle;
import com.example.parking.vehicle.domain.model.VehicleStatus;
import com.example.parking.vehicle.domain.model.VehicleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.parking.parkingsession.application.port.in.IGetParkingSessionHistoryUseCase;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSessionService implements
        ICheckInVehicleUseCase,
        ICheckOutVehicleUseCase,
        IGetParkingSessionUseCase,
        IGetParkingSessionHistoryUseCase {

    private static final String VISITOR_OWNER_NAME = "VISITOR";
    private final ICreateInvoiceUseCase createInvoiceUseCase;
    private final IParkingSessionRepositoryPort parkingSessionRepositoryPort;
    private final IVehicleRepositoryPort vehicleRepositoryPort;
    private final IParkingSlotManagementPort parkingSlotManagementPort;
    private final IParkingFeeCalculatorPort parkingFeeCalculatorPort;

    public ParkingSessionService(
            IParkingSessionRepositoryPort parkingSessionRepositoryPort,
            IVehicleRepositoryPort vehicleRepositoryPort,
            IParkingSlotManagementPort parkingSlotManagementPort,
            IParkingFeeCalculatorPort parkingFeeCalculatorPort,
            ICreateInvoiceUseCase createInvoiceUseCase
    ) {
        this.parkingSessionRepositoryPort = parkingSessionRepositoryPort;
        this.vehicleRepositoryPort = vehicleRepositoryPort;
        this.parkingSlotManagementPort = parkingSlotManagementPort;
        this.parkingFeeCalculatorPort = parkingFeeCalculatorPort;
        this.createInvoiceUseCase = createInvoiceUseCase;
    }

    @Override
    @Transactional
    public ParkingSession checkIn(String plateNumber, VehicleType requestVehicleType) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        Optional<Vehicle> vehicleOptional = vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber);

        Vehicle vehicleToUse;
        VehicleType vehicleTypeToUse;

        if (vehicleOptional.isPresent()) {
            Vehicle registeredVehicle = vehicleOptional.get();

            validateRegisteredVehicleCanCheckIn(registeredVehicle);

            vehicleToUse = registeredVehicle;
            vehicleTypeToUse = registeredVehicle.getType();
        } else {
            if (requestVehicleType == null) {
                throw new BusinessException("Vehicle type is required for visitor vehicle");
            }

            Vehicle visitorVehicle = new Vehicle(
                    null,
                    normalizedPlateNumber,
                    requestVehicleType,
                    VISITOR_OWNER_NAME,
                    null,
                    VehicleStatus.APPROVED,
                    null,
                    null,
                    null
            );

            vehicleToUse = vehicleRepositoryPort.save(visitorVehicle);
            vehicleTypeToUse = requestVehicleType;
        }

        if (parkingSessionRepositoryPort.existsActiveByVehicleId(vehicleToUse.getId())) {
            throw new BusinessException("Vehicle already has an active parking session");
        }

        ParkingSlot availableSlot = parkingSlotManagementPort
                .findAvailableSlotByVehicleType(vehicleTypeToUse)
                .orElseThrow(() -> new BusinessException("No available parking slot for this vehicle type"));

        ParkingSession newSession = new ParkingSession(
                null,
                vehicleToUse.getId(),
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

        Vehicle vehicle = vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber)
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
        createInvoiceUseCase.createInvoiceAfterCheckOut(
                savedSession.getId(),
                vehicle.getOwnerUserId(),
                pricingResult.getPlateNumber(),
                pricingResult.getVehicleType(),
                pricingResult.getTotalAmount()
        );

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
    public List<ParkingSession> getActiveParkingSessions() {
        return parkingSessionRepositoryPort.findByStatus(ParkingSessionStatus.ACTIVE);
    }

    @Override
    public ParkingSession getActiveSessionByPlateNumber(String plateNumber) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        Vehicle vehicle = vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        return parkingSessionRepositoryPort.findActiveByVehicleId(vehicle.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active parking session not found"));
    }

    @Override
    public ParkingSessionDetail getParkingSessionDetailById(Long parkingSessionId) {
        ParkingSession parkingSession = parkingSessionRepositoryPort.findById(parkingSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking session not found"));

        return toDetail(parkingSession);
    }

    @Override
    public List<ParkingSessionDetail> getAllParkingSessionDetails() {
        return parkingSessionRepositoryPort.findAll()
                .stream()
                .map(this::toDetail)
                .toList();
    }

    @Override
    public List<ParkingSessionDetail> getActiveParkingSessionDetails() {
        return parkingSessionRepositoryPort.findByStatus(ParkingSessionStatus.ACTIVE)
                .stream()
                .map(this::toDetail)
                .toList();
    }

    @Override
    public ParkingSessionDetail getActiveSessionDetailByPlateNumber(String plateNumber) {
        ParkingSession parkingSession = getActiveSessionByPlateNumber(plateNumber);

        return toDetail(parkingSession);
    }
    @Override
        public List<ParkingSessionHistoryItem> getHistory(
                String plateNumber,
                String status,
                LocalDate fromDate,
                LocalDate toDate
        ) {
        ParkingSessionStatus parsedStatus = parseStatus(status);
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTimeExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();

        String normalizedPlateNumberFilter = normalizeOptionalPlateNumber(plateNumber);

        return parkingSessionRepositoryPort.findAllOrderByCheckInTimeDesc()
                .stream()
                .map(this::toHistoryItem)
                .filter(item -> matchesPlateNumber(item, normalizedPlateNumberFilter))
                .filter(item -> matchesStatus(item, parsedStatus))
                .filter(item -> matchesDateRange(item, fromDateTime, toDateTimeExclusive))
                .toList();
        }

    private ParkingSessionDetail toDetail(ParkingSession parkingSession) {
        Vehicle vehicle = vehicleRepositoryPort.findById(parkingSession.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        ParkingSlot parkingSlot = parkingSlotManagementPort.findById(parkingSession.getParkingSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found"));

        return new ParkingSessionDetail(
                parkingSession.getId(),
                parkingSession.getVehicleId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                parkingSession.getParkingSlotId(),
                parkingSlot.getSlotCode(),
                parkingSession.getCheckInTime(),
                parkingSession.getCheckOutTime(),
                parkingSession.getStatus()
        );
    }

    private void validateRegisteredVehicleCanCheckIn(Vehicle vehicle) {
        VehicleStatus status = vehicle.getStatus();

        if (status == null || status == VehicleStatus.APPROVED) {
            return;
        }

        if (status == VehicleStatus.PENDING) {
            throw new BusinessException("Vehicle is pending approval");
        }

        if (status == VehicleStatus.REJECTED) {
            throw new BusinessException("Vehicle registration was rejected");
        }

        if (status == VehicleStatus.BLOCKED) {
            throw new BusinessException("Vehicle is blocked");
        }

        throw new BusinessException("Invalid vehicle status");
    }

    private String normalizePlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }

        return plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
    }
    private ParkingSessionHistoryItem toHistoryItem(ParkingSession parkingSession) {
    Optional<Vehicle> vehicleOptional = vehicleRepositoryPort.findById(
            parkingSession.getVehicleId()
    );

    Optional<ParkingSlot> parkingSlotOptional = parkingSlotManagementPort.findById(
            parkingSession.getParkingSlotId()
    );

    String plateNumber = vehicleOptional
            .map(Vehicle::getPlateNumber)
            .orElse(null);

    VehicleType vehicleType = vehicleOptional
            .map(Vehicle::getType)
            .orElse(null);

    String slotCode = parkingSlotOptional
            .map(ParkingSlot::getSlotCode)
            .orElse(null);

    Long durationHours = null;
    BigDecimal totalAmount = null;

    if (parkingSession.getStatus() == ParkingSessionStatus.COMPLETED
            && parkingSession.getCheckOutTime() != null
            && vehicleOptional.isPresent()) {

        PricingResult pricingResult = parkingFeeCalculatorPort.calculateFee(
                parkingSession.getId()
        );

        durationHours = pricingResult.getDurationHours();
        totalAmount = pricingResult.getTotalAmount();
    }

    return new ParkingSessionHistoryItem(
            parkingSession.getId(),
            plateNumber,
            vehicleType,
            slotCode,
            parkingSession.getCheckInTime(),
            parkingSession.getCheckOutTime(),
            durationHours,
            totalAmount,
            parkingSession.getStatus()
    );
}

private ParkingSessionStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
        return null;
    }

    try {
        return ParkingSessionStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
        throw new BusinessException("Invalid parking session status");
    }
}

private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
        throw new BusinessException("fromDate must be before or equal to toDate");
    }
}

private String normalizeOptionalPlateNumber(String plateNumber) {
    if (plateNumber == null || plateNumber.isBlank()) {
        return null;
    }

    return plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
}

private boolean matchesPlateNumber(
        ParkingSessionHistoryItem item,
        String normalizedPlateNumberFilter
) {
    if (normalizedPlateNumberFilter == null) {
        return true;
    }

    if (item.getPlateNumber() == null) {
        return false;
    }

    String itemPlateNumber = item.getPlateNumber()
            .trim()
            .toUpperCase()
            .replaceAll("\\s+", "");

    return itemPlateNumber.contains(normalizedPlateNumberFilter);
}

private boolean matchesStatus(
        ParkingSessionHistoryItem item,
        ParkingSessionStatus status
) {
    if (status == null) {
        return true;
    }

    return item.getStatus() == status;
}

private boolean matchesDateRange(
        ParkingSessionHistoryItem item,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTimeExclusive
) {
    LocalDateTime checkInTime = item.getCheckInTime();

    if (checkInTime == null) {
        return false;
    }

    if (fromDateTime != null && checkInTime.isBefore(fromDateTime)) {
        return false;
    }

    if (toDateTimeExclusive != null && !checkInTime.isBefore(toDateTimeExclusive)) {
        return false;
    }

    return true;
}
}