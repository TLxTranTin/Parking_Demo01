package com.example.parking.buildingstructure.adapter.in.web;

import com.example.parking.buildingstructure.adapter.in.web.dto.CreateZoneRequest;
import com.example.parking.buildingstructure.adapter.in.web.dto.ZoneResponse;
import com.example.parking.buildingstructure.application.port.in.ICreateZoneUseCase;
import com.example.parking.buildingstructure.application.port.in.IListZonesUseCase;
import com.example.parking.buildingstructure.application.port.in.ISetZoneActiveUseCase;
import com.example.parking.buildingstructure.domain.model.BuildingZone;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/building/zones")
public class BuildingZoneController {

    private final ICreateZoneUseCase createZoneUseCase;
    private final IListZonesUseCase listZonesUseCase;
    private final ISetZoneActiveUseCase setZoneActiveUseCase;

    public BuildingZoneController(
            ICreateZoneUseCase createZoneUseCase,
            IListZonesUseCase listZonesUseCase,
            ISetZoneActiveUseCase setZoneActiveUseCase
    ) {
        this.createZoneUseCase = createZoneUseCase;
        this.listZonesUseCase = listZonesUseCase;
        this.setZoneActiveUseCase = setZoneActiveUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> createZone(
            @Valid @RequestBody CreateZoneRequest request
    ) {
        BuildingZone zone = createZoneUseCase.createZone(
                request.getFloorId(),
                request.getZoneCode(),
                request.getZoneName(),
                request.getVehicleType(),
                request.getDescription()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Zone created successfully", toResponse(zone))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getZones(
            @RequestParam(required = false) Long floorId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String vehicleType
    ) {
        List<ZoneResponse> zones = listZonesUseCase.getZones(
                        floorId,
                        active,
                        vehicleType
                )
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get zones successfully", zones)
        );
    }

    @PatchMapping("/{id}/inactive")
    public ResponseEntity<ApiResponse<ZoneResponse>> inactiveZone(@PathVariable Long id) {
        BuildingZone zone = setZoneActiveUseCase.setZoneActive(id, false);

        return ResponseEntity.ok(
                ApiResponse.success("Zone inactive successfully", toResponse(zone))
        );
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<ZoneResponse>> activeZone(@PathVariable Long id) {
        BuildingZone zone = setZoneActiveUseCase.setZoneActive(id, true);

        return ResponseEntity.ok(
                ApiResponse.success("Zone active successfully", toResponse(zone))
        );
    }

    private ZoneResponse toResponse(BuildingZone zone) {
        return new ZoneResponse(
                zone.getId(),
                zone.getFloorId(),
                zone.getFloorCode(),
                zone.getFloorName(),
                zone.getZoneCode(),
                zone.getZoneName(),
                zone.getVehicleType(),
                zone.getDescription(),
                zone.isActive(),
                zone.getCreatedAt(),
                zone.getUpdatedAt()
        );
    }
}