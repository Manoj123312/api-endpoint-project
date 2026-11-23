package com.georgetravel.booking;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

record BookingRequest(
    @NotBlank @Size(max = 100) String customerName,
    @NotBlank @Size(max = 200) String pickup,
    @NotBlank @Size(max = 200) String destination,
    @NotNull @DecimalMin("0.1") @DecimalMax("10000") @Digits(integer = 5, fraction = 2) BigDecimal distanceKm,
    @NotNull CabType cabType
) {}
