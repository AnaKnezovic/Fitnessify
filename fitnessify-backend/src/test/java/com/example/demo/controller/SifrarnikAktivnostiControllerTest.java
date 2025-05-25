package com.example.demo.controller;

import com.example.demo.model.Aktivnost;
import com.example.demo.repository.AktivnostRepository;
import com.example.demo.repository.KorisnikAktivnostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SifrarnikAktivnostiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AktivnostRepository aktivnostRepo;
    @MockBean KorisnikAktivnostRepository korisnikAktRepo;

    private Aktivnost makeAktivnost(Long id, String vrsta, Double kcal) {
        Aktivnost a = new Aktivnost();
        a.setId(id);
        a.setVrstaAktivnosti(vrsta);
        a.setKalorijePoSatu(kcal);
        return a;
    }

    @Test @DisplayName("GET /api/sifrarnik-aktivnosti -> list all")
    void getAll() throws Exception {
        Aktivnost a1 = makeAktivnost(1L, "Trčanje", 600.0);
        when(aktivnostRepo.findAll()).thenReturn(List.of(a1));

        mockMvc.perform(get("/api/sifrarnik-aktivnosti"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].vrstaAktivnosti").value("Trčanje"));
    }

    @Test @DisplayName("GET /api/sifrarnik-aktivnosti/{id} -> found")
    void getById_found() throws Exception {
        Aktivnost a = makeAktivnost(42L, "Bicikl", 400.0);
        when(aktivnostRepo.findById(42L)).thenReturn(Optional.of(a));

        mockMvc.perform(get("/api/sifrarnik-aktivnosti/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.vrstaAktivnosti").value("Bicikl"));
    }

    @Test @DisplayName("GET /api/sifrarnik-aktivnosti/{id} -> not found")
    void getById_notFound() throws Exception {
        when(aktivnostRepo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sifrarnik-aktivnosti/99"))
            .andExpect(status().isNotFound());
    }

    @Test @DisplayName("POST /api/sifrarnik-aktivnosti -> create new")
    void create_success() throws Exception {
        Aktivnost toCreate = makeAktivnost(null, "Plivanje", 500.0);
        Aktivnost saved    = makeAktivnost(5L, "Plivanje", 500.0);

        when(aktivnostRepo.existsByVrstaAktivnosti("Plivanje")).thenReturn(false);
        when(aktivnostRepo.save(ArgumentMatchers.any())).thenReturn(saved);

        mockMvc.perform(post("/api/sifrarnik-aktivnosti")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.vrstaAktivnosti").value("Plivanje"));
    }

    @Test @DisplayName("POST /api/sifrarnik-aktivnosti -> fail if exists")
    void create_conflict() throws Exception {
        Aktivnost toCreate = makeAktivnost(null, "Pilates", 200.0);
        when(aktivnostRepo.existsByVrstaAktivnosti("Pilates")).thenReturn(true);

        mockMvc.perform(post("/api/sifrarnik-aktivnosti")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
            .andExpect(status().is4xxClientError());
    }

    @Test @DisplayName("PUT /api/sifrarnik-aktivnosti/{id} -> update when not in use")
    void update_success() throws Exception {
        Aktivnost existing = makeAktivnost(7L, "Joga", 250.0);
        Aktivnost details  = makeAktivnost(null, "Hot joga", 300.0);

        when(aktivnostRepo.findById(7L)).thenReturn(Optional.of(existing));
        when(korisnikAktRepo.existsByAktivnost(existing)).thenReturn(false);
        when(aktivnostRepo.save(existing)).thenReturn(existing);

        mockMvc.perform(put("/api/sifrarnik-aktivnosti/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.vrstaAktivnosti").value("Hot joga"))
            .andExpect(jsonPath("$.kalorijePoSatu").value(300.0));
    }

    @Test @DisplayName("PUT /api/sifrarnik-aktivnosti/{id} -> fail when in use")
    void update_inUse() throws Exception {
        Aktivnost existing = makeAktivnost(8L, "HIIT", 700.0);
        when(aktivnostRepo.findById(8L)).thenReturn(Optional.of(existing));
        when(korisnikAktRepo.existsByAktivnost(existing)).thenReturn(true);

        mockMvc.perform(put("/api/sifrarnik-aktivnosti/8")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"vrstaAktivnosti\":\"HIIT X\",\"kalorijePoSatu\":750}"))
            .andExpect(status().is4xxClientError());
    }

    @Test @DisplayName("DELETE /api/sifrarnik-aktivnosti/{id} -> delete when not in use")
    void delete_success() throws Exception {
        Aktivnost existing = makeAktivnost(9L, "Crossfit", 800.0);
        when(aktivnostRepo.findById(9L)).thenReturn(Optional.of(existing));
        when(korisnikAktRepo.existsByAktivnost(existing)).thenReturn(false);

        mockMvc.perform(delete("/api/sifrarnik-aktivnosti/9"))
            .andExpect(status().isNoContent());

        verify(aktivnostRepo).delete(existing);
    }

    @Test @DisplayName("DELETE /api/sifrarnik-aktivnosti/{id} -> fail when in use")
    void delete_inUse() throws Exception {
        Aktivnost existing = makeAktivnost(10L, "Box", 650.0);
        when(aktivnostRepo.findById(10L)).thenReturn(Optional.of(existing));
        when(korisnikAktRepo.existsByAktivnost(existing)).thenReturn(true);

        mockMvc.perform(delete("/api/sifrarnik-aktivnosti/10"))
            .andExpect(status().is4xxClientError());
    }

    @Test @DisplayName("DELETE /api/sifrarnik-aktivnosti/{id} -> not found")
    void delete_notFound() throws Exception {
        when(aktivnostRepo.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/sifrarnik-aktivnosti/123"))
            .andExpect(status().is4xxClientError());
    }
}

