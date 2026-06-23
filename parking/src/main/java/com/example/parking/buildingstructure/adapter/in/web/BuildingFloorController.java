package com.example.parking.buildingstructure.adapter.in.web;

import com.example.parking.buildingstructure.adapter.in.web.dto.CreateFloorRequest;
import com.example.parking.buildingstructure.adapter.in.web.dto.FloorResponse;
import com.example.parking.buildingstructure.application.port.in.ICreateFloorUseCase;
import com.example.parking.buildingstructure.application.port.in.IListFloorsUseCase;
import com.example.parking.buildingstructure.application.port.in.ISetFloorActiveUseCase;
import com.example.parking.buildingstructure.domain.model.BuildingFloor;
import com.example.parking.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/building/floors")
public class BuildingFloorController {

    private final ICreateFloorUseCase createFloorUseCase;
    private final IListFloorsUseCase listFloorsUseCase;
    private final ISetFloorActiveUseCase setFloorActiveUseCase;

    public BuildingFloorController(
            ICreateFloorUseCase createFloorUseCase,
            IListFloorsUseCase listFloorsUseCase,
            ISetFloorActiveUseCase setFloorActiveUseCase
    ) {
        this.createFloorUseCase = createFloorUseCase;
        this.listFloorsUseCase = listFloorsUseCase;
        this.setFloorActiveUseCase = setFloorActiveUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FloorResponse>> createFloor(
            @Valid @RequestBody CreateFloorRequest request
    ) {
        BuildingFloor floor = createFloorUseCase.createFloor(
                request.getFloorCode(),
                request.getFloorName(),
                request.getDescription()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Floor created successfully", toResponse(floor))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getFloors(
            @RequestParam(required = false) Boolean active
    ) {
        List<FloorResponse> floors = listFloorsUseCase.getFloors(active)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get floors successfully", floors)
        );
    }

    @PatchMapping("/{id}/inactive")
    public ResponseEntity<ApiResponse<FloorResponse>> inactiveFloor(@PathVariable Long id) {
        BuildingFloor floor = setFloorActiveUseCase.setFloorActive(id, false);

        return ResponseEntity.ok(
                ApiResponse.success("Floor inactive successfully", toResponse(floor))
        );
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<FloorResponse>> activeFloor(@PathVariable Long id) {
        BuildingFloor floor = setFloorActiveUseCase.setFloorActive(id, true);

        return ResponseEntity.ok(
                ApiResponse.success("Floor active successfully", toResponse(floor))
        );
    }

    private FloorResponse toResponse(BuildingFloor floor) {
        return new FloorResponse(
                floor.getId(),
                floor.getFloorCode(),
                floor.getFloorName(),
                floor.getDescription(),
                floor.isActive(),
                floor.getCreatedAt(),
                floor.getUpdatedAt()
        );
    }
}