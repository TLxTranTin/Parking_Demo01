package com.example.parking.payment.adapter.out.persistence;

import com.example.parking.payment.domain.model.PaymentMethod;
import com.example.parking.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 30)
    private PaymentMethod method;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "payment_id", nullable = false)
    private List<PaymentInvoiceItemEntity> invoiceItems = new ArrayList<>();

    protected PaymentEntity() {
    }

    public PaymentEntity(
            Long id,
            Long userId,
            BigDecimal amount,
            PaymentStatus status,
            PaymentMethod method,
            LocalDateTime paidAt,
            List<PaymentInvoiceItemEntity> invoiceItems
    ) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.paidAt = paidAt;
        this.invoiceItems = invoiceItems == null ? new ArrayList<>() : invoiceItems;
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

    public List<PaymentInvoiceItemEntity> getInvoiceItems() {
        return invoiceItems;
    }
}