package com.example.parking.payment.application.port.in;

import com.example.parking.payment.domain.model.Payment;
import com.example.parking.payment.domain.model.PaymentMethod;

import java.util.List;

public interface IPayMyInvoicesUseCase {

    Payment payMyInvoices(
            Long currentUserId,
            List<Long> invoiceIds,
            PaymentMethod method
    );
}