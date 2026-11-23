package com.georgetravel.fare;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FareController.class)
class FareControllerTest {
    @Autowired MockMvc mvc;

    @Test void calculatesEachCabFare() throws Exception {
        String[] types = {"STANDARD", "SUV", "LUXURY"};
        double[] totals = {200.0, 275.0, 362.5};
        for (int i = 0; i < types.length; i++) {
            mvc.perform(post("/api/fares/quote").contentType(MediaType.APPLICATION_JSON)
                .content("{\"distanceKm\":12.5,\"cabType\":\"" + types[i] + "\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalFare").value(totals[i]))
                .andExpect(jsonPath("$.currency").value("INR"));
        }
    }

    @Test void rejectsInvalidInput() throws Exception {
        for (String body : new String[]{"{\"distanceKm\":0,\"cabType\":\"STANDARD\"}",
                "{\"distanceKm\":1.234,\"cabType\":\"SUV\"}", "{\"distanceKm\":1,\"cabType\":\"BUS\"}", "{}"}) {
            mvc.perform(post("/api/fares/quote").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
    }
}
