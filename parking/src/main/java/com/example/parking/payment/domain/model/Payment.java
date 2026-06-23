package com.example.parking.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Payment {

    private final Long id;
    private final Long userId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final PaymentMethod method;
    private final LocalDateTime paidAt;
    private final List<Long> invoiceIds;

    public Payment(
            Long id,
            Long userId,
            BigDecimal amount,
            PaymentStatus status,
            PaymentMethod method,
            LocalDateTime paidAt,
            List<Long> invoiceIds
    ) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.paidAt = paidAt;
        this.invoiceIds = invoiceIds == null ? List.of() : List.copyOf(invoiceIds);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public List<Long> getInvoiceIds() {
        return invoiceIds;
    }
}