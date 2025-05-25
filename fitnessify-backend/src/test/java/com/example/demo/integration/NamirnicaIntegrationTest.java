package com.example.demo.integration;

import com.example.demo.model.Namirnica;
import com.example.demo.repository.NamirnicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;



@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class NamirnicaIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private NamirnicaRepository namirnicaRepo;

    @BeforeEach
    void cleanUp() {
        namirnicaRepo.deleteAll();
    }

    @Test
    @DisplayName("CRUD cycle on /api/namirnice")
    void fullCrudCycle() throws Exception {
        // --- CREATE ---
        String createJson = """
            {
              "naziv":"TestFood",
              "kalorije":123,
              "proteini":10.5,
              "ugljikohidrati":20.5,
              "masti":5.5
            }
            """;

        // POST /api/namirnice
        mockMvc.perform(post("/api/namirnice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.naziv").value("TestFood"))
            .andExpect(jsonPath("$.kalorije").value(123))
            .andExpect(jsonPath("$.proteini").value(10.5))
            .andExpect(jsonPath("$.ugljikohidrati").value(20.5))
            .andExpect(jsonPath("$.masti").value(5.5));

        // Grab the generated ID
        Long id = namirnicaRepo.findAll()
            .stream()
            .findFirst()
            .map(Namirnica::getId)
            .orElseThrow();

        // --- READ ALL & READ ONE ---
        mockMvc.perform(get("/api/namirnice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(id));

        mockMvc.perform(get("/api/namirnice/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.naziv").value("TestFood"));

        // --- UPDATE ---
        String updateJson = """
            {
              "naziv":"UpdatedFood",
              "kalorije":200,
              "proteini":15.0,
              "ugljikohidrati":25.0,
              "masti":7.0
            }
            """;

        mockMvc.perform(put("/api/namirnice/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.naziv").value("UpdatedFood"))
            .andExpect(jsonPath("$.kalorije").value(200));

        // --- DELETE ---
        mockMvc.perform(delete("/api/namirnice/{id}", id))
            .andExpect(status().isNoContent());

        // After delete, GET should 404
        mockMvc.perform(get("/api/namirnice/{id}", id))
            .andExpect(status().isNotFound());
    }
}
