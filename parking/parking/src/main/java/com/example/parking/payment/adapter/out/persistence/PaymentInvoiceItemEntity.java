package com.example.parking.payment.adapter.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_invoice_items")
public class PaymentInvoiceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    protected PaymentInvoiceItemEntity() {
    }

    public PaymentInvoiceItemEntity(Long id, Long invoiceId) {
        this.id = id;
        this.invoiceId = invoiceId;
    }

    public PaymentInvoiceItemEntity(Long invoiceId) {
        this(null, invoiceId);
    }

    public Long getId() {
        return id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }
}