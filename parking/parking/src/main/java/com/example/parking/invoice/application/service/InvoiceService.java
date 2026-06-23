package com.example.parking.invoice.application.service;

import com.example.parking.invoice.application.port.in.ICreateInvoiceUseCase;
import com.example.parking.invoice.application.port.in.IGetInvoiceUseCase;
import com.example.parking.invoice.application.port.out.IInvoiceRepositoryPort;
import com.example.parking.invoice.domain.model.Invoice;
import com.example.parking.invoice.domain.model.InvoiceStatus;
import com.example.parking.invoice.domain.model.InvoiceType;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.vehicle.domain.model.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService implements ICreateInvoiceUseCase, IGetInvoiceUseCase {

    private final IInvoiceRepositoryPort invoiceRepositoryPort;

    public InvoiceService(IInvoiceRepositoryPort invoiceRepositoryPort) {
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    @Override
    public Invoice createInvoiceAfterCheckOut(
            Long parkingSessionId,
            Long userId,
            String plateNumber,
            VehicleType vehicleType,
            BigDecimal amount
    ) {
        if (parkingSessionId == null) {
            throw new BusinessException("Parking session id is required");
        }

        if (plateNumber == null || plateNumber.isBlank()) {
            throw new BusinessException("Plate number is required");
        }

        if (vehicleType == null) {
            throw new BusinessException("Vehicle type is required");
        }

        if (amount == null) {
            throw new BusinessException("Invoice amount is required");
        }

        if (invoiceRepositoryPort.existsByParkingSessionId(parkingSessionId)) {
            return invoiceRepositoryPort.findByParkingSessionId(parkingSessionId)
                    .orElseThrow(() -> new BusinessException("Invoice already exists"));
        }

        LocalDateTime now = LocalDateTime.now();

        InvoiceType invoiceType = userId == null
                ? InvoiceType.GUEST
                : InvoiceType.REGISTERED_USER;

        InvoiceStatus invoiceStatus = userId == null
                ? InvoiceStatus.PAID
                : InvoiceStatus.UNPAID;

        LocalDateTime paidAt = userId == null ? now : null;

        Invoice invoice = new Invoice(
                null,
                parkingSessionId,
                userId,
                plateNumber.trim().toUpperCase().replaceAll("\\s+", ""),
                vehicleType,
                amount,
                invoiceStatus,
                invoiceType,
                now,
                paidAt
        );

        return invoiceRepositoryPort.save(invoice);
    }

    @Override
    public List<Invoice> getInvoices(
            String plateNumber,
            String status,
            String type,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        InvoiceStatus parsedStatus = parseStatus(status);
        InvoiceType parsedType = parseType(type);
        validateDateRange(fromDate, toDate);

        String normalizedPlateNumber = normalizeOptionalPlateNumber(plateNumber);
        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTimeExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();

        return invoiceRepositoryPort.findAllOrderByIssuedAtDesc()
                .stream()
                .filter(invoice -> matchesPlateNumber(invoice, normalizedPlateNumber))
                .filter(invoice -> matchesStatus(invoice, parsedStatus))
                .filter(invoice -> matchesType(invoice, parsedType))
                .filter(invoice -> matchesDateRange(invoice, fromDateTime, toDateTimeExclusive))
                .toList();
    }

    @Override
    public List<Invoice> getMyInvoices(Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        return invoiceRepositoryPort.findByUserIdOrderByIssuedAtDesc(currentUserId);
    }

    private InvoiceStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return InvoiceStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid invoice status");
        }
    }

    private InvoiceType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }

        try {
            return InvoiceType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid invoice type");
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

    private boolean matchesPlateNumber(Invoice invoice, String normalizedPlateNumber) {
        if (normalizedPlateNumber == null) {
            return true;
        }

        if (invoice.getPlateNumber() == null) {
            return false;
        }

        String invoicePlateNumber = invoice.getPlateNumber()
                .trim()
                .toUpperCase()
                .replaceAll("\\s+", "");

        return invoicePlateNumber.contains(normalizedPlateNumber);
    }

    private boolean matchesStatus(Invoice invoice, InvoiceStatus status) {
        if (status == null) {
            return true;
        }

        return invoice.getStatus() == status;
    }

    private boolean matchesType(Invoice invoice, InvoiceType type) {
        if (type == null) {
            return true;
        }

        return invoice.getType() == type;
    }

    private boolean matchesDateRange(
            Invoice invoice,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTimeExclusive
    ) {
        LocalDateTime issuedAt = invoice.getIssuedAt();

        if (issuedAt == null) {
            return false;
        }

        if (fromDateTime != null && issuedAt.isBefore(fromDateTime)) {
            return false;
        }

        if (toDateTimeExclusive != null && !issuedAt.isBefore(toDateTimeExclusive)) {
            return false;
        }

        return true;
    }
}