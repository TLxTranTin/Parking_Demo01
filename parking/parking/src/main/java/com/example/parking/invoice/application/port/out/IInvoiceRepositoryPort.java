package com.example.parking.invoice.application.port.out;

import com.example.parking.invoice.domain.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface IInvoiceRepositoryPort {

    Invoice save(Invoice invoice);

    boolean existsByParkingSessionId(Long parkingSessionId);

    Optional<Invoice> findByParkingSessionId(Long parkingSessionId);

    List<Invoice> findAllOrderByIssuedAtDesc();

    List<Invoice> findByUserIdOrderByIssuedAtDesc(Long userId);
    
    Optional<Invoice> findById(Long id);
}