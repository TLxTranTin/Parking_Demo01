package com.example.parking.invoice.adapter.out.persistence;

import com.example.parking.invoice.application.port.out.IInvoiceRepositoryPort;
import com.example.parking.invoice.domain.model.Invoice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InvoicePersistenceAdapter implements IInvoiceRepositoryPort {

    private final ISpringDataInvoiceRepository springDataInvoiceRepository;

    public InvoicePersistenceAdapter(ISpringDataInvoiceRepository springDataInvoiceRepository) {
        this.springDataInvoiceRepository = springDataInvoiceRepository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = toEntity(invoice);
        InvoiceEntity savedEntity = springDataInvoiceRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public boolean existsByParkingSessionId(Long parkingSessionId) {
        return springDataInvoiceRepository.existsByParkingSessionId(parkingSessionId);
    }

    @Override
    public Optional<Invoice> findByParkingSessionId(Long parkingSessionId) {
        return springDataInvoiceRepository.findByParkingSessionId(parkingSessionId)
                .map(this::toDomain);
    }

    @Override
    public List<Invoice> findAllOrderByIssuedAtDesc() {
        return springDataInvoiceRepository.findAllByOrderByIssuedAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Invoice> findByUserIdOrderByIssuedAtDesc(Long userId) {
        return springDataInvoiceRepository.findByUserIdOrderByIssuedAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return springDataInvoiceRepository.findById(id)
                .map(this::toDomain);
    }

    private Invoice toDomain(InvoiceEntity entity) {
        return new Invoice(
                entity.getId(),
                entity.getParkingSessionId(),
                entity.getUserId(),
                entity.getPlateNumber(),
                entity.getVehicleType(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getType(),
                entity.getIssuedAt(),
                entity.getPaidAt()
        );
    }

    private InvoiceEntity toEntity(Invoice invoice) {
        return new InvoiceEntity(
                invoice.getId(),
                invoice.getParkingSessionId(),
                invoice.getUserId(),
                invoice.getPlateNumber(),
                invoice.getVehicleType(),
                invoice.getAmount(),
                invoice.getStatus(),
                invoice.getType(),
                invoice.getIssuedAt(),
                invoice.getPaidAt()
        );
    }
}