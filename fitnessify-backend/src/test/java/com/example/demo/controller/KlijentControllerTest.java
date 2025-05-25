package com.example.demo.controller;

import com.example.demo.model.Klijent;
import com.example.demo.model.Korisnik;
import com.example.demo.repository.KlijentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class KlijentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KlijentRepository klijentRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/klijenti/{id} – postoji klijent")
    void getKlijent_found() throws Exception {
        // Priprema Korisnika
        Korisnik korisnik = new Korisnik();
        korisnik.setId(42L);
        korisnik.setIme("Ivana");
        korisnik.setPrezime("Ivić");
        korisnik.setEmail("ivana@example.com");
        korisnik.setLozinka("tajna");
        korisnik.setSpol("Ž");
        korisnik.setDob(28);
        korisnik.setDatumRegistracije(LocalDate.of(2024,1,1));

        // Priprema Klijenta
        Klijent klijent = new Klijent();
        klijent.setId(42L);
        klijent.setKorisnik(korisnik);
        klijent.setVisina(170.0);
        klijent.setTezina(65.5);
        klijent.setCilj("Mršavljenje");

        when(klijentRepo.findById(42L)).thenReturn(Optional.of(klijent));

        mockMvc.perform(get("/api/klijenti/42")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.visina").value(170.0))
            .andExpect(jsonPath("$.tezina").value(65.5))
            .andExpect(jsonPath("$.cilj").value("Mršavljenje"))
            .andExpect(jsonPath("$.korisnik.id").value(42))
            .andExpect(jsonPath("$.korisnik.ime").value("Ivana"))
            .andExpect(jsonPath("$.korisnik.prezime").value("Ivić"));
    }

    @Test
    @DisplayName("GET /api/klijenti/{id} – ne postoji klijent")
    void getKlijent_notFound() throws Exception {
        when(klijentRepo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/klijenti/99"))
            // orElseThrow baci NoSuchElementException → HTTP 500
            .andExpect(status().is4xxClientError());
    }
}
