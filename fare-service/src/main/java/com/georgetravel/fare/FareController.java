package com.georgetravel.fare;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fares")
public class FareController {
    enum CabType { STANDARD, SUV, LUXURY }

    record QuoteRequest(@NotNull @DecimalMin("0.1") @DecimalMax("10000") @Digits(integer = 5, fraction = 2) BigDecimal distanceKm,
                        @NotNull CabType cabType) {}
    record QuoteResponse(BigDecimal distanceKm, CabType cabType, BigDecimal baseFare,
                         BigDecimal ratePerKm, BigDecimal totalFare, String currency) {}

    @PostMapping("/quote")
    QuoteResponse quote(@Valid @RequestBody QuoteRequest request) {
        BigDecimal base = new BigDecimal("50.00");
        BigDecimal rate = switch (request.cabType()) {
            case STANDARD -> new BigDecimal("12.00");
            case SUV -> new BigDecimal("18.00");
            case LUXURY -> new BigDecimal("25.00");
        };
        BigDecimal total = base.add(rate.multiply(request.distanceKm())).setScale(2, RoundingMode.HALF_UP);
        return new QuoteResponse(request.distanceKm(), request.cabType(), base, rate, total, "INR");
    }
}
