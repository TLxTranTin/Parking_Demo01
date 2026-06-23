package com.example.parking.parkingslot.adapter.in.web.dto;

import com.example.parking.parkingslot.domain.model.SlotStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateParkingSlotStatusRequest {

    @NotNull(message = "Status is required")
    private SlotStatus status;

    public UpdateParkingSlotStatusRequest() {
    }

    public UpdateParkingSlotStatusRequest(SlotStatus status) {
        this.status = status;
    }

    public SlotStatus getStatus() {
        return status;
    }
}