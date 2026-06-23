package com.example.parking.invoice.adapter.in.web;

import com.example.parking.invoice.adapter.in.web.dto.InvoiceResponse;
import com.example.parking.invoice.application.port.in.IGetInvoiceUseCase;
import com.example.parking.invoice.domain.model.Invoice;
import com.example.parking.shared.response.ApiResponse;
import com.example.parking.shared.security.CurrentUserPrincipal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final IGetInvoiceUseCase getInvoiceUseCase;

    public InvoiceController(IGetInvoiceUseCase getInvoiceUseCase) {
        this.getInvoiceUseCase = getInvoiceUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getInvoices(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<InvoiceResponse> invoices = getInvoiceUseCase.getInvoices(
                        plateNumber,
                        status,
                        type,
                        fromDate,
                        toDate
                )
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get invoices successfully", invoices)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getMyInvoices(
            Authentication authentication
    ) {
        CurrentUserPrincipal currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Unauthorized"));
        }

        List<InvoiceResponse> invoices = getInvoiceUseCase.getMyInvoices(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success("Get my invoices successfully", invoices)
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

    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
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