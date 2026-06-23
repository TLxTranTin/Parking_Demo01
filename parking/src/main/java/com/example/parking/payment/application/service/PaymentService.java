package com.example.parking.payment.application.service;

import com.example.parking.invoice.application.port.out.IInvoiceRepositoryPort;
import com.example.parking.invoice.domain.model.Invoice;
import com.example.parking.invoice.domain.model.InvoiceStatus;
import com.example.parking.invoice.domain.model.InvoiceType;
import com.example.parking.payment.application.port.in.IGetPaymentUseCase;
import com.example.parking.payment.application.port.in.IPayMyInvoicesUseCase;
import com.example.parking.payment.application.port.out.IPaymentRepositoryPort;
import com.example.parking.payment.domain.model.Payment;
import com.example.parking.payment.domain.model.PaymentMethod;
import com.example.parking.payment.domain.model.PaymentStatus;
import com.example.parking.shared.exception.BusinessException;
import com.example.parking.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class PaymentService implements IPayMyInvoicesUseCase, IGetPaymentUseCase {

    private final IPaymentRepositoryPort paymentRepositoryPort;
    private final IInvoiceRepositoryPort invoiceRepositoryPort;

    public PaymentService(
            IPaymentRepositoryPort paymentRepositoryPort,
            IInvoiceRepositoryPort invoiceRepositoryPort
    ) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.invoiceRepositoryPort = invoiceRepositoryPort;
    }

    @Override
    @Transactional
    public Payment payMyInvoices(
            Long currentUserId,
            List<Long> invoiceIds,
            PaymentMethod method
    ) {
        if (currentUserId == null) {
            throw new BusinessException("Current user id is required");
        }

        if (invoiceIds == null || invoiceIds.isEmpty()) {
            throw new BusinessException("Invoice ids are required");
        }

        List<Long> normalizedInvoiceIds = invoiceIds.stream()
                .filter(id -> id != null)
                .toList();

        if (normalizedInvoiceIds.isEmpty()) {
            throw new BusinessException("Invoice ids are required");
        }

        List<Long> uniqueInvoiceIds = new LinkedHashSet<>(normalizedInvoiceIds)
                .stream()
                .toList();

        PaymentMethod paymentMethod = method == null ? PaymentMethod.MOCK : method;
        LocalDateTime now = LocalDateTime.now();

        List<Invoice> invoices = uniqueInvoiceIds.stream()
                .map(this::findInvoiceById)
                .toList();

        validateInvoicesBelongToCurrentUser(invoices, currentUserId);
        validateInvoicesCanBePaid(invoices);

        BigDecimal totalAmount = invoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = new Payment(
                null,
                currentUserId,
                totalAmount,
                PaymentStatus.SUCCESS,
                paymentMethod,
                now,
                uniqueInvoiceIds
        );

        Payment savedPayment = paymentRepositoryPort.save(payment);

        invoices.stream()
                .map(invoice -> markInvoiceAsPaid(invoice, now))
                .forEach(invoiceRepositoryPort::save);

        return savedPayment;
    }

    @Override
    public List<Payment> getPayments(
            Long userId,
            String status,
            String method,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        PaymentStatus parsedStatus = parseStatus(status);
        PaymentMethod parsedMethod = parseMethod(method);
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateTimeExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();

        return paymentRepositoryPort.findAllOrderByPaidAtDesc()
                .stream()
                .filter(payment -> matchesUserId(payment, userId))
                .filter(payment -> matchesStatus(payment, parsedStatus))
                .filter(payment -> matchesMethod(payment, parsedMethod))
                .filter(payment -> matchesDateRange(payment, fromDateTime, toDateTimeExclusive))
                .toList();
    }

    private Invoice findInvoiceById(Long invoiceId) {
        return invoiceRepositoryPort.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    private void validateInvoicesBelongToCurrentUser(
            List<Invoice> invoices,
            Long currentUserId
    ) {
        for (Invoice invoice : invoices) {
            if (invoice.getUserId() == null || !invoice.getUserId().equals(currentUserId)) {
                throw new BusinessException("Invoice does not belong to current user");
            }
        }
    }

    private void validateInvoicesCanBePaid(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            if (invoice.getStatus() == InvoiceStatus.PAID) {
                throw new BusinessException("Invoice is already paid");
            }

            if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
                throw new BusinessException("Cancelled invoice cannot be paid");
            }

            if (invoice.getStatus() != InvoiceStatus.UNPAID) {
                throw new BusinessException("Only unpaid invoice can be paid");
            }

            if (invoice.getType() != InvoiceType.REGISTERED_USER) {
                throw new BusinessException("Only registered user invoice can be paid");
            }
        }
    }

    private Invoice markInvoiceAsPaid(Invoice invoice, LocalDateTime paidAt) {
        return new Invoice(
                invoice.getId(),
                invoice.getParkingSessionId(),
                invoice.getUserId(),
                invoice.getPlateNumber(),
                invoice.getVehicleType(),
                invoice.getAmount(),
                InvoiceStatus.PAID,
                invoice.getType(),
                invoice.getIssuedAt(),
                paidAt
        );
    }

    private PaymentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return PaymentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid payment status");
        }
    }

    private PaymentMethod parseMethod(String method) {
        if (method == null || method.isBlank()) {
            return null;
        }

        try {
            return PaymentMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid payment method");
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException("fromDate must be before or equal to toDate");
        }
    }

    private boolean matchesUserId(Payment payment, Long userId) {
        if (userId == null) {
            return true;
        }

        return payment.getUserId() != null && payment.getUserId().equals(userId);
    }

    private boolean matchesStatus(Payment payment, PaymentStatus status) {
        if (status == null) {
            return true;
        }

        return payment.getStatus() == status;
    }

    private boolean matchesMethod(Payment payment, PaymentMethod method) {
        if (method == null) {
            return true;
        }

        return payment.getMethod() == method;
    }

    private boolean matchesDateRange(
            Payment payment,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTimeExclusive
    ) {
        LocalDateTime paidAt = payment.getPaidAt();

        if (paidAt == null) {
            return false;
        }

        if (fromDateTime != null && paidAt.isBefore(fromDateTime)) {
            return false;
        }

        if (toDateTimeExclusive != null && !paidAt.isBefore(toDateTimeExclusive)) {
            return false;
        }

        return true;
    }
}