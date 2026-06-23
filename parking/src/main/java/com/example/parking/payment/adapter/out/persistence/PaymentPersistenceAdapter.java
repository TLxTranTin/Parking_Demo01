package com.example.parking.payment.adapter.out.persistence;

import com.example.parking.payment.application.port.out.IPaymentRepositoryPort;
import com.example.parking.payment.domain.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentPersistenceAdapter implements IPaymentRepositoryPort {

    private final ISpringDataPaymentRepository springDataPaymentRepository;

    public PaymentPersistenceAdapter(ISpringDataPaymentRepository springDataPaymentRepository) {
        this.springDataPaymentRepository = springDataPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = toEntity(payment);
        PaymentEntity savedEntity = springDataPaymentRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Payment> findAllOrderByPaidAtDesc() {
        return springDataPaymentRepository.findAllByOrderByPaidAtDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Payment toDomain(PaymentEntity entity) {
        List<Long> invoiceIds = entity.getInvoiceItems()
                .stream()
                .map(PaymentInvoiceItemEntity::getInvoiceId)
                .toList();

        return new Payment(
                entity.getId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getMethod(),
                entity.getPaidAt(),
                invoiceIds
        );
    }

    private PaymentEntity toEntity(Payment payment) {
        List<PaymentInvoiceItemEntity> invoiceItems = payment.getInvoiceIds()
                .stream()
                .map(PaymentInvoiceItemEntity::new)
                .toList();

        return new PaymentEntity(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getPaidAt(),
                invoiceItems
        );
    }
}