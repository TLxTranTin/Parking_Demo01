package com.example.parking.vehicle.application.service;

import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.application.port.in.IApproveVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IBlockVehicleUseCase;
import com.example.parking.vehicle.application.port.in.ICreateVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IDeleteVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IGetVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IRegisterVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IRejectVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IUnblockVehicleUseCase;
import com.example.parking.vehicle.application.port.in.IUpdateVehicleUseCase;
import com.example.parking.vehicle.application.port.out.IVehicleRepositoryPort;
import com.example.parking.vehicle.domain.model.Vehicle;
import com.example.parking.vehicle.domain.model.VehicleStatus;
import com.example.parking.vehicle.domain.model.VehicleType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService implements
        ICreateVehicleUseCase,
        IGetVehicleUseCase,
        IUpdateVehicleUseCase,
        IDeleteVehicleUseCase,
        IRegisterVehicleUseCase,
        IApproveVehicleUseCase,
        IRejectVehicleUseCase,
        IBlockVehicleUseCase,
        IUnblockVehicleUseCase {

    private final IVehicleRepositoryPort vehicleRepositoryPort;

    public VehicleService(IVehicleRepositoryPort vehicleRepositoryPort) {
        this.vehicleRepositoryPort = vehicleRepositoryPort;
    }

    @Override
    public Vehicle createVehicle(String plateNumber, VehicleType type, String ownerName) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);
        String normalizedOwnerName = normalizeOwnerName(ownerName);

        if (type == null) {
            throw new BusinessException("Vehicle type is required");
        }

        if (vehicleRepositoryPort.existsByPlateNumber(normalizedPlateNumber)) {
            throw new BusinessException("Plate number already exists");
        }

        Vehicle vehicle = new Vehicle(
                null,
                normalizedPlateNumber,
                type,
                normalizedOwnerName,
                null,
                VehicleStatus.APPROVED,
                null,
                null,
                null
        );

        return vehicleRepositoryPort.save(vehicle);
    }

    @Override
    public Vehicle registerVehicle(
            Long currentUserId,
            String username,
            String plateNumber,
            VehicleType type,
            String brand,
            String color,
            String description
    ) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        String normalizedUsername = normalizeUsername(username);
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        if (type == null) {
            throw new BusinessException("Vehicle type is required");
        }

        if (vehicleRepositoryPort.existsByPlateNumber(normalizedPlateNumber)) {
            throw new BusinessException("Plate number already exists");
        }

        String normalizedBrand = normalizeOptionalText(brand);
        String normalizedColor = normalizeOptionalText(color);
        String normalizedDescription = normalizeOptionalText(description);

        Vehicle vehicle = new Vehicle(
                null,
                normalizedPlateNumber,
                type,
                normalizedUsername,
                currentUserId,
                VehicleStatus.PENDING,
                normalizedBrand,
                normalizedColor,
                normalizedDescription
        );

        return vehicleRepositoryPort.save(vehicle);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepositoryPort.findAll();
    }

    @Override
    public Vehicle getVehicleByPlateNumber(String plateNumber) {
        String normalizedPlateNumber = normalizePlateNumber(plateNumber);

        return vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    @Override
    public List<Vehicle> getVehiclesForCurrentUser(Long currentUserId, String currentRole) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        String role = normalizeRole(currentRole);

        if (isAdminOrStaff(role)) {
            return vehicleRepositoryPort.findAll();
        }

        if (isUser(role)) {
            return vehicleRepositoryPort.findByOwnerUserId(currentUserId);
        }

        throw new BusinessException("Invalid role");
    }

    @Override
    public Vehicle searchVehicleByPlateNumberForCurrentUser(
            String plateNumber,
            Long currentUserId,
            String currentRole
    ) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        String role = normalizeRole(currentRole);

        if (isUser(role)) {
            throw new BusinessException("USER cannot search vehicle by plate number");
        }

        if (!isAdminOrStaff(role)) {
            throw new BusinessException("Invalid role");
        }

        return getVehicleByPlateNumber(plateNumber);
    }

    @Override
    public Vehicle updateVehicle(Long id, String plateNumber, VehicleType type, String ownerName) {
        Vehicle currentVehicle = findVehicleById(id);

        String normalizedPlateNumber = normalizePlateNumber(plateNumber);
        String normalizedOwnerName = normalizeOwnerName(ownerName);

        if (type == null) {
            throw new BusinessException("Vehicle type is required");
        }

        vehicleRepositoryPort.findByPlateNumber(normalizedPlateNumber)
                .ifPresent(existingVehicle -> {
                    boolean isAnotherVehicle = !existingVehicle.getId().equals(currentVehicle.getId());

                    if (isAnotherVehicle) {
                        throw new BusinessException("Plate number already exists");
                    }
                });

        Vehicle updatedVehicle = new Vehicle(
                currentVehicle.getId(),
                normalizedPlateNumber,
                type,
                normalizedOwnerName,
                currentVehicle.getOwnerUserId(),
                currentVehicle.getStatus(),
                currentVehicle.getBrand(),
                currentVehicle.getColor(),
                currentVehicle.getDescription()
        );

        return vehicleRepositoryPort.save(updatedVehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        if (!vehicleRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found");
        }

        vehicleRepositoryPort.deleteById(id);
    }

    @Override
    public Vehicle approveVehicle(Long vehicleId) {
        Vehicle currentVehicle = findVehicleById(vehicleId);

        if (currentVehicle.getStatus() != VehicleStatus.PENDING) {
            throw new BusinessException("Only pending vehicle can be approved");
        }

        Vehicle approvedVehicle = changeStatus(currentVehicle, VehicleStatus.APPROVED);

        return vehicleRepositoryPort.save(approvedVehicle);
    }

    @Override
    public Vehicle rejectVehicle(Long vehicleId) {
        Vehicle currentVehicle = findVehicleById(vehicleId);

        if (currentVehicle.getStatus() != VehicleStatus.PENDING) {
            throw new BusinessException("Only pending vehicle can be rejected");
        }

        Vehicle rejectedVehicle = changeStatus(currentVehicle, VehicleStatus.REJECTED);

        return vehicleRepositoryPort.save(rejectedVehicle);
    }

    @Override
    public Vehicle blockVehicle(Long vehicleId) {
        Vehicle currentVehicle = findVehicleById(vehicleId);

        if (currentVehicle.getStatus() != VehicleStatus.APPROVED) {
            throw new BusinessException("Only approved vehicle can be blocked");
        }

        Vehicle blockedVehicle = changeStatus(currentVehicle, VehicleStatus.BLOCKED);

        return vehicleRepositoryPort.save(blockedVehicle);
    }

    @Override
    public Vehicle unblockVehicle(Long vehicleId) {
        Vehicle currentVehicle = findVehicleById(vehicleId);

        if (currentVehicle.getStatus() != VehicleStatus.BLOCKED) {
            throw new BusinessException("Only blocked vehicle can be unblocked");
        }

        Vehicle unblockedVehicle = changeStatus(currentVehicle, VehicleStatus.APPROVED);

        return vehicleRepositoryPort.save(unblockedVehicle);
    }

    private Vehicle findVehicleById(Long vehicleId) {
        return vehicleRepositoryPort.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    private Vehicle changeStatus(Vehicle vehicle, VehicleStatus newStatus) {
        return new Vehicle(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                vehicle.getOwnerName(),
                vehicle.getOwnerUserId(),
                newStatus,
                vehicle.getBrand(),
                vehicle.getColor(),
                vehicle.getDescription()
        );
    }

    private String normalizePlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }

        return plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
    }

    private String normalizeOwnerName(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new BusinessException("Owner name is required");
        }

        return ownerName.trim();
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("Username is required");
        }

        return username.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            throw new BusinessException("Invalid role");
        }

        if (role.startsWith("ROLE_")) {
            return role.substring(5);
        }

        return role;
    }

    private boolean isAdminOrStaff(String role) {
        return isAdmin(role) || isStaff(role);
    }

    private boolean isAdmin(String role) {
        return "ADMIN".equals(role);
    }

    private boolean isStaff(String role) {
        return "STAFF".equals(role);
    }

    private boolean isUser(String role) {
        return "USER".equals(role);
    }
}