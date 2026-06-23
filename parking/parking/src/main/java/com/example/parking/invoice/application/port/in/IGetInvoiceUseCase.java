package com.example.parking.invoice.application.port.in;

import com.example.parking.invoice.domain.model.Invoice;

import java.time.LocalDate;
import java.util.List;

public interface IGetInvoiceUseCase {

    List<Invoice> getInvoices(
            String plateNumber,
            String status,
            String type,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<Invoice> getMyInvoices(Long currentUserId);
}