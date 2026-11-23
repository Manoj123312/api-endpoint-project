package com.georgetravel.booking;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
class FareClient {
    private final RestClient client;

    FareClient(RestClient.Builder builder, @Value("${fare.service.url}") String url) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        client = builder.baseUrl(url).requestFactory(factory).build();
    }

    FareQuote quote(BigDecimal distanceKm, CabType cabType) {
        try {
            FareQuote quote = client.post().uri("/api/fares/quote")
                .body(Map.of("distanceKm", distanceKm, "cabType", cabType))
                .retrieve().body(FareQuote.class);
            if (quote == null || quote.totalFare() == null || quote.currency() == null
                    || quote.distanceKm() == null || quote.distanceKm().compareTo(distanceKm) != 0
                    || quote.cabType() != cabType || quote.totalFare().signum() < 0
                    || quote.totalFare().scale() > 2 || !quote.currency().equals("INR")) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Fare service returned an invalid quote");
            }
            return quote;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Fare service is unavailable", ex);
        }
    }
}
