package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AktivnostControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AktivnostRepository aktivnostRepo;
    @MockBean private KorisnikAktivnostRepository korisnikAktRepo;
    @MockBean private KorisnikRepository korisnikRepo;

    @Test @DisplayName("GET /api/aktivnosti – vraća sve aktivnosti")
    void getAllAktivnosti() throws Exception {
        Aktivnost a = new Aktivnost();
        a.setId(1L);
        a.setVrstaAktivnosti("Trčanje");
        a.setKalorijePoSatu(600.0);
        when(aktivnostRepo.findAll()).thenReturn(List.of(a));

        mockMvc.perform(get("/api/aktivnosti"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].vrstaAktivnosti").value("Trčanje"));
    }

    @Test @DisplayName("POST /api/aktivnosti/korisnik – dodaje korisnik aktivnost")
    void addKorisnikAktivnost() throws Exception {
        Map<String,Object> body = Map.of(
            "korisnikId", 5,
            "aktivnostId", 7,
            "trajanje", 30,
            "datum", "2025-05-25"
        );

        Korisnik k = new Korisnik(); k.setId(5L);
        Aktivnost act = new Aktivnost(); act.setId(7L); act.setKalorijePoSatu(300.0);

        when(korisnikRepo.findById(5L)).thenReturn(Optional.of(k));
        when(aktivnostRepo.findById(7L)).thenReturn(Optional.of(act));
        when(korisnikAktRepo.save(any(KorisnikAktivnost.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/aktivnosti/korisnik")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.korisnik.id").value(5))
            .andExpect(jsonPath("$.aktivnost.id").value(7))
            // 300 * 30 / 60 = 150
            .andExpect(jsonPath("$.potroseneKalorije").value(150.0));
    }

    @Test @DisplayName("GET /api/aktivnosti/korisnik/{id}/{datum} – vraća dnevne aktivnosti")
    void getKorisnikAktivnostiZaDan() throws Exception {
        LocalDate d = LocalDate.of(2025,5,25);
        KorisnikAktivnost ka = new KorisnikAktivnost();
        ka.setId(new KorisnikAktivnostId(5L,7L,d));
        when(korisnikAktRepo.findByKorisnikIdAndIdDatumAktivnosti(5L, d))
            .thenReturn(List.of(ka));

        mockMvc.perform(get("/api/aktivnosti/korisnik/5/2025-05-25"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id.korisnikId").value(5))
            .andExpect(jsonPath("$[0].id.aktivnostId").value(7));
    }

    @Test @DisplayName("DELETE /api/aktivnosti/korisnik – briše korisnik aktivnost")
    void deleteKorisnikAktivnost() throws Exception {
        mockMvc.perform(delete("/api/aktivnosti/korisnik")
                .param("korisnikId","5")
                .param("aktivnostId","7")
                .param("datum","2025-05-25"))
            .andExpect(status().isOk());

        verify(korisnikAktRepo)
            .deleteById(new KorisnikAktivnostId(5L,7L,LocalDate.parse("2025-05-25")));
    }

    @Test @DisplayName("PUT /api/aktivnosti/korisnik – update korisnik aktivnosti")
    void updateKorisnikAktivnost() throws Exception {
        LocalDate d = LocalDate.of(2025,5,25);
        KorisnikAktivnostId id = new KorisnikAktivnostId(5L,7L,d);
        Aktivnost act = new Aktivnost(); act.setId(7L); act.setKalorijePoSatu(120.0);
        Korisnik k = new Korisnik(); k.setId(5L);
        KorisnikAktivnost ka = new KorisnikAktivnost();
        ka.setId(id);
        ka.setAktivnost(act);
        ka.setKorisnik(k);
        ka.setTrajanje(30);
        ka.setPotroseneKalorije(60.0);

        when(korisnikAktRepo.findById(id)).thenReturn(Optional.of(ka));
        when(korisnikAktRepo.save(any(KorisnikAktivnost.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        Map<String,Object> body = Map.of(
            "korisnikId",5,
            "aktivnostId",7,
            "trajanje",45,
            "datum","2025-05-25"
        );

        mockMvc.perform(put("/api/aktivnosti/korisnik")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trajanje").value(45))
            // 120 * 45 / 60 = 90
            .andExpect(jsonPath("$.potroseneKalorije").value(90.0));
    }

    @Test @DisplayName("GET /api/aktivnosti/klijent/{id} – sve aktivnosti klijenta")
    void getSveAktivnostiKlijenta() throws Exception {
        KorisnikAktivnost ka1 = new KorisnikAktivnost();
        ka1.setId(new KorisnikAktivnostId(5L,7L,LocalDate.now()));
        when(korisnikAktRepo.findByKorisnikId(5L)).thenReturn(List.of(ka1));

        mockMvc.perform(get("/api/aktivnosti/klijent/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id.korisnikId").value(5));
    }
}
