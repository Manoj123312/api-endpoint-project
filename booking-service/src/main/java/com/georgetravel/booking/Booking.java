package com.georgetravel.booking;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String customerName;
    @Column(nullable = false, length = 200)
    private String pickup;
    @Column(nullable = false, length = 200)
    private String destination;
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal distanceKm;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private CabType cabType;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFare;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private Instant createdAt;

    protected Booking() {}

    Booking(BookingRequest request, FareQuote quote) {
        customerName = request.customerName().trim();
        pickup = request.pickup().trim();
        destination = request.destination().trim();
        distanceKm = request.distanceKm();
        cabType = request.cabType();
        totalFare = quote.totalFare();
        currency = quote.currency();
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getPickup() { return pickup; }
    public String getDestination() { return destination; }
    public BigDecimal getDistanceKm() { return distanceKm; }
    public CabType getCabType() { return cabType; }
    public BigDecimal getTotalFare() { return totalFare; }
    public String getCurrency() { return currency; }
    public Instant getCreatedAt() { return createdAt; }
}
