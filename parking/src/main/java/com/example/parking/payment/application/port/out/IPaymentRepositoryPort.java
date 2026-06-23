package com.example.parking.payment.application.port.out;

import com.example.parking.payment.domain.model.Payment;

import java.util.List;

public interface IPaymentRepositoryPort {

    Payment save(Payment payment);

    List<Payment> findAllOrderByPaidAtDesc();
}