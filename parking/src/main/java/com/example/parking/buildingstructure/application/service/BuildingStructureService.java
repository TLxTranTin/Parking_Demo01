package com.example.parking.buildingstructure.application.service;

import com.example.parking.buildingstructure.application.port.in.ICreateFloorUseCase;
import com.example.parking.buildingstructure.application.port.in.ICreateZoneUseCase;
import com.example.parking.buildingstructure.application.port.in.IListFloorsUseCase;
import com.example.parking.buildingstructure.application.port.in.IListZonesUseCase;
import com.example.parking.buildingstructure.application.port.in.ISetFloorActiveUseCase;
import com.example.parking.buildingstructure.application.port.in.ISetZoneActiveUseCase;
import com.example.parking.buildingstructure.application.port.out.IBuildingFloorRepositoryPort;
import com.example.parking.buildingstructure.application.port.out.IBuildingZoneRepositoryPort;
import com.example.parking.buildingstructure.domain.model.BuildingFloor;
import com.example.parking.buildingstructure.domain.model.BuildingZone;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import com.example.parking.vehicle.domain.model.VehicleType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BuildingStructureService implements
        ICreateFloorUseCase,
        IListFloorsUseCase,
        ISetFloorActiveUseCase,
        ICreateZoneUseCase,
        IListZonesUseCase,
        ISetZoneActiveUseCase {

    private final IBuildingFloorRepositoryPort buildingFloorRepositoryPort;
    private final IBuildingZoneRepositoryPort buildingZoneRepositoryPort;

    public BuildingStructureService(
            IBuildingFloorRepositoryPort buildingFloorRepositoryPort,
            IBuildingZoneRepositoryPort buildingZoneRepositoryPort
    ) {
        this.buildingFloorRepositoryPort = buildingFloorRepositoryPort;
        this.buildingZoneRepositoryPort = buildingZoneRepositoryPort;
    }

    @Override
    public BuildingFloor createFloor(
            String floorCode,
            String floorName,
            String description
    ) {
        String normalizedFloorCode = normalizeCode(floorCode, "Floor code is required");
        String normalizedFloorName = normalizeRequiredText(floorName, "Floor name is required");
        String normalizedDescription = normalizeOptionalText(description);

        if (buildingFloorRepositoryPort.existsByFloorCode(normalizedFloorCode)) {
            throw new BusinessException("Floor code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        BuildingFloor floor = new BuildingFloor(
                null,
                normalizedFloorCode,
                normalizedFloorName,
                normalizedDescription,
                true,
                now,
                now
        );

        return buildingFloorRepositoryPort.save(floor);
    }

    @Override
    public List<BuildingFloor> getFloors(Boolean active) {
        return buildingFloorRepositoryPort.findAllOrderByCreatedAtDesc()
                .stream()
                .filter(floor -> active == null || floor.isActive() == active)
                .toList();
    }

    @Override
    public BuildingFloor setFloorActive(Long id, boolean active) {
        BuildingFloor currentFloor = getFloorOrThrow(id);

        BuildingFloor updatedFloor = new BuildingFloor(
                currentFloor.getId(),
                currentFloor.getFloorCode(),
                currentFloor.getFloorName(),
                currentFloor.getDescription(),
                active,
                currentFloor.getCreatedAt(),
                LocalDateTime.now()
        );

        return buildingFloorRepositoryPort.save(updatedFloor);
    }

    @Override
    public BuildingZone createZone(
            Long floorId,
            String zoneCode,
            String zoneName,
            String vehicleType,
            String description
    ) {
        if (floorId == null) {
            throw new BusinessException("Floor id is required");
        }

        BuildingFloor floor = getFloorOrThrow(floorId);

        if (!floor.isActive()) {
            throw new BusinessException("Building floor is inactive");
        }

        String normalizedZoneCode = normalizeCode(zoneCode, "Zone code is required");
        String normalizedZoneName = normalizeRequiredText(zoneName, "Zone name is required");
        VehicleType parsedVehicleType = parseOptionalVehicleType(vehicleType);
        String normalizedDescription = normalizeOptionalText(description);

        if (buildingZoneRepositoryPort.existsByFloorIdAndZoneCode(floorId, normalizedZoneCode)) {
            throw new BusinessException("Zone code already exists in this floor");
        }

        LocalDateTime now = LocalDateTime.now();

        BuildingZone zone = new BuildingZone(
                null,
                floor.getId(),
                floor.getFloorCode(),
                floor.getFloorName(),
                normalizedZoneCode,
                normalizedZoneName,
                parsedVehicleType,
                normalizedDescription,
                true,
                now,
                now
        );

        return buildingZoneRepositoryPort.save(zone);
    }

    @Override
    public List<BuildingZone> getZones(
            Long floorId,
            Boolean active,
            String vehicleType
    ) {
        VehicleType parsedVehicleType = parseOptionalVehicleType(vehicleType);

        return buildingZoneRepositoryPort.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::enrichZoneWithFloor)
                .filter(zone -> floorId == null || zone.getFloorId().equals(floorId))
                .filter(zone -> active == null || zone.isActive() == active)
                .filter(zone -> parsedVehicleType == null || zone.getVehicleType() == parsedVehicleType)
                .toList();
    }

    @Override
    public BuildingZone getZoneById(Long id) {
        BuildingZone zone = buildingZoneRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building zone not found"));

        return enrichZoneWithFloor(zone);
    }

    @Override
    public BuildingZone setZoneActive(Long id, boolean active) {
        BuildingZone currentZone = getZoneById(id);

        BuildingZone updatedZone = new BuildingZone(
                currentZone.getId(),
                currentZone.getFloorId(),
                currentZone.getFloorCode(),
                currentZone.getFloorName(),
                currentZone.getZoneCode(),
                currentZone.getZoneName(),
                currentZone.getVehicleType(),
                currentZone.getDescription(),
                active,
                currentZone.getCreatedAt(),
                LocalDateTime.now()
        );

        return buildingZoneRepositoryPort.save(updatedZone);
    }

    private BuildingFloor getFloorOrThrow(Long id) {
        return buildingFloorRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building floor not found"));
    }

    private BuildingZone enrichZoneWithFloor(BuildingZone zone) {
        BuildingFloor floor = getFloorOrThrow(zone.getFloorId());

        return new BuildingZone(
                zone.getId(),
                zone.getFloorId(),
                floor.getFloorCode(),
                floor.getFloorName(),
                zone.getZoneCode(),
                zone.getZoneName(),
                zone.getVehicleType(),
                zone.getDescription(),
                zone.isActive(),
                zone.getCreatedAt(),
                zone.getUpdatedAt()
        );
    }

    private VehicleType parseOptionalVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.isBlank()) {
            return null;
        }

        try {
            return VehicleType.valueOf(vehicleType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid vehicle type");
        }
    }

    private String normalizeCode(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(message);
        }

        return value.trim().toUpperCase().replaceAll("\\s+", "");
    }

    private String normalizeRequiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(message);
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}