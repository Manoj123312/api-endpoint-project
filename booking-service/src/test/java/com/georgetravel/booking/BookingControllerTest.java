package com.georgetravel.booking;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean BookingRepository repository;
    @MockitoBean FareClient fareClient;

    private static final String VALID = "{\"customerName\":\"Alex\",\"pickup\":\"Station\",\"destination\":\"Airport\",\"distanceKm\":12.5,\"cabType\":\"STANDARD\"}";

    @Test void createsBookingUsingServerQuote() throws Exception {
        when(fareClient.quote(any(), eq(CabType.STANDARD))).thenReturn(new FareQuote(new BigDecimal("12.5"), CabType.STANDARD, new BigDecimal("50"), new BigDecimal("12"), new BigDecimal("200"), "INR"));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(VALID))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.customerName").value("Alex"))
            .andExpect(jsonPath("$.totalFare").value(200));
        verify(repository).save(any(Booking.class));
    }

    @Test void rejectsInvalidBookingsWithoutSaving() throws Exception {
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerName\":\" \",\"pickup\":\"A\",\"destination\":\"B\",\"distanceKm\":0,\"cabType\":\"STANDARD\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(fareClient, repository);
    }

    @Test void doesNotSaveWhenFareServiceFails() throws Exception {
        when(fareClient.quote(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Fare service is unavailable"));
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(VALID))
            .andExpect(status().isBadGateway());
        verifyNoInteractions(repository);
    }

    @Test void returnsNotFoundForUnknownBooking() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/bookings/99")).andExpect(status().isNotFound());
    }
}
