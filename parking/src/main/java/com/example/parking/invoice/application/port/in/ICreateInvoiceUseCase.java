package com.example.parking.invoice.application.port.in;

import com.example.parking.invoice.domain.model.Invoice;
import com.example.parking.vehicle.domain.model.VehicleType;

import java.math.BigDecimal;

public interface ICreateInvoiceUseCase {

    Invoice createInvoiceAfterCheckOut(
            Long parkingSessionId,
            Long userId,
            String plateNumber,
            VehicleType vehicleType,
            BigDecimal amount
    );
}