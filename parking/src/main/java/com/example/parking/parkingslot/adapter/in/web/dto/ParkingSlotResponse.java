package com.example.parking.parkingslot.adapter.in.web.dto;

import com.example.parking.parkingslot.domain.model.SlotStatus;
import com.example.parking.vehicle.domain.model.VehicleType;

public class ParkingSlotResponse {

    private final Long id;
    private final String slotCode;
    private final String floor;
    private final VehicleType vehicleType;
    private final SlotStatus status;

    private final Long zoneId;
    private final String zoneCode;
    private final String zoneName;
    private final Long floorId;
    private final String floorCode;
    private final String floorName;

    public ParkingSlotResponse(
            Long id,
            String slotCode,
            String floor,
            VehicleType vehicleType,
            SlotStatus status
    ) {
        this(
                id,
                slotCode,
                floor,
                vehicleType,
                status,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public ParkingSlotResponse(
            Long id,
            String slotCode,
            String floor,
            VehicleType vehicleType,
            SlotStatus status,
            Long zoneId,
            String zoneCode,
            String zoneName,
            Long floorId,
            String floorCode,
            String floorName
    ) {
        this.id = id;
        this.slotCode = slotCode;
        this.floor = floor;
        this.vehicleType = vehicleType;
        this.status = status;
        this.zoneId = zoneId;
        this.zoneCode = zoneCode;
        this.zoneName = zoneName;
        this.floorId = floorId;
        this.floorCode = floorCode;
        this.floorName = floorName;
    }

    public Long getId() { return id; }

    public String getSlotCode() { return slotCode; }

    public String getFloor() { return floor; }

    public VehicleType getVehicleType() { return vehicleType; }

    public SlotStatus getStatus() { return status; }

    public Long getZoneId() { return zoneId; }

    public String getZoneCode() { return zoneCode; }

    public String getZoneName() { return zoneName; }

    public Long getFloorId() { return floorId; }

    public String getFloorCode() { return floorCode; }

    public String getFloorName() { return floorName; }
}