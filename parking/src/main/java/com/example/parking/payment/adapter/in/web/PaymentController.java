package com.example.parking.payment.adapter.in.web;

import com.example.parking.payment.adapter.in.web.dto.PayMyInvoicesRequest;
import com.example.parking.payment.adapter.in.web.dto.PaymentResponse;
import com.example.parking.payment.application.port.in.IGetPaymentUseCase;
import com.example.parking.payment.application.port.in.IPayMyInvoicesUseCase;
import com.example.parking.payment.domain.model.Payment;
import com.example.parking.shared.response.ApiResponse;
import com.example.parking.shared.security.CurrentUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IPayMyInvoicesUseCase payMyInvoicesUseCase;
    private final IGetPaymentUseCase getPaymentUseCase;

    public PaymentController(
            IPayMyInvoicesUseCase payMyInvoicesUseCase,
            IGetPaymentUseCase getPaymentUseCase
    ) {
        this.payMyInvoicesUseCase = payMyInvoicesUseCase;
        this.getPaymentUseCase = getPaymentUseCase;
    }

    @PostMapping("/my-invoices")
    public ResponseEntity<ApiResponse<PaymentResponse>> payMyInvoices(
            @Valid @RequestBody PayMyInvoicesRequest request,
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }

        Payment payment = payMyInvoicesUseCase.payMyInvoices(
                currentUser.getId(),
                request.getInvoiceIds(),
                request.getMethod()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Payment successfully", toResponse(payment))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<PaymentResponse> payments = getPaymentUseCase.getPayments(
                        userId,
                        status,
                        method,
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get payments successfully", payments)
        );
    }

    private CurrentUserPrincipal getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CurrentUserPrincipal currentUser)) {
            return null;
        }

        return currentUser;
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getPaidAt(),
                payment.getInvoiceIds()
        );
    }
}