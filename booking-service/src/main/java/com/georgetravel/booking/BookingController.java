package com.georgetravel.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingRepository repository;
    private final FareClient fareClient;

    BookingController(BookingRepository repository, FareClient fareClient) {
        this.repository = repository;
        this.fareClient = fareClient;
    }

    record QuoteRequest(@NotNull @DecimalMin("0.1") @DecimalMax("10000") @Digits(integer = 5, fraction = 2) BigDecimal distanceKm,
                        @NotNull CabType cabType) {}

    @PostMapping("/fare-quote")
    FareQuote quote(@Valid @RequestBody QuoteRequest request) {
        return fareClient.quote(request.distanceKm(), request.cabType());
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    Booking create(@Valid @RequestBody BookingRequest request) {
        FareQuote quote = fareClient.quote(request.distanceKm(), request.cabType());
        return repository.save(new Booking(request, quote));
    }

    @GetMapping("/bookings")
    List<Booking> list() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @GetMapping("/bookings/{id}")
    Booking get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }
}
