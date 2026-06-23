package com.example.parking.payment.adapter.in.web.dto;

import com.example.parking.payment.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PayMyInvoicesRequest {

    @NotEmpty(message = "Invoice ids are required")
    private List<Long> invoiceIds;

    private PaymentMethod method;

    public PayMyInvoicesRequest() {
    }

    public PayMyInvoicesRequest(List<Long> invoiceIds, PaymentMethod method) {
        this.invoiceIds = invoiceIds;
        this.method = method;
    }

    public List<Long> getInvoiceIds() {
        return invoiceIds;
    }

    public PaymentMethod getMethod() {
        return method;
    }
}