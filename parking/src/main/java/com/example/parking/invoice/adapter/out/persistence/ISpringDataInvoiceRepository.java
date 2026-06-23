package com.example.parking.invoice.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISpringDataInvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    boolean existsByParkingSessionId(Long parkingSessionId);

    Optional<InvoiceEntity> findByParkingSessionId(Long parkingSessionId);

    List<InvoiceEntity> findAllByOrderByIssuedAtDesc();

    List<InvoiceEntity> findByUserIdOrderByIssuedAtDesc(Long userId);
}