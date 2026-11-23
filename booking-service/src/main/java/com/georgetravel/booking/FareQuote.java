package com.georgetravel.booking;

import java.math.BigDecimal;

record FareQuote(BigDecimal distanceKm, CabType cabType, BigDecimal baseFare,
                 BigDecimal ratePerKm, BigDecimal totalFare, String currency) {}
