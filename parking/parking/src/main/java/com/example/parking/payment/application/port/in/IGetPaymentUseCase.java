package com.example.parking.payment.application.port.in;

import com.example.parking.payment.domain.model.Payment;

import java.time.LocalDate;
import java.util.List;

public interface IGetPaymentUseCase {

    List<Payment> getPayments(
            Long userId,
            String status,
            String method,
            LocalDate fromDate,
            LocalDate toDate
    );
}