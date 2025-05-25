package com.example.demo.controller;

import com.example.demo.dto.StatistikaDTO;
import com.example.demo.service.StatistikaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class StatistikaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatistikaService statistikaService;

    private List<StatistikaDTO> sampleData() {
        return List.of(
            new StatistikaDTO(LocalDate.of(2025,5,1), 2000.0, 1800.0),
            new StatistikaDTO(LocalDate.of(2025,5,2), 2100.0, 1900.0)
        );
    }

    @Test
    @DisplayName("GET /api/statistika/mjesecna – returns monthly stats for korisnik")
    void mjesecnaStatistika_success() throws Exception {
        long korisnikId = 42L;
        when(statistikaService.dohvatiMjesecnuStatistiku(korisnikId))
            .thenReturn(sampleData());

        mockMvc.perform(get("/api/statistika/mjesecna")
                .param("korisnikId", String.valueOf(korisnikId))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].datum").value("2025-05-01"))
            .andExpect(jsonPath("$[0].unosKalorija").value(2000.0))
            .andExpect(jsonPath("$[0].potrosnjaKalorija").value(1800.0))
            .andExpect(jsonPath("$[0].bilanca").value(200.0))
            .andExpect(jsonPath("$[1].datum").value("2025-05-02"))
            .andExpect(jsonPath("$[1].bilanca").value(200.0));
    }

    @Test
    @DisplayName("GET /api/statistika/mjesecna-klijent – returns monthly stats for klijent")
    void mjesecnaStatistikaKlijent_success() throws Exception {
        long klijentId = 99L;
        when(statistikaService.dohvatiMjesecnuStatistiku(klijentId))
            .thenReturn(sampleData());

        mockMvc.perform(get("/api/statistika/mjesecna-klijent")
                .param("klijentId", String.valueOf(klijentId))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].datum").value("2025-05-01"))
            .andExpect(jsonPath("$[1].bilanca").value(200.0));
    }
}

