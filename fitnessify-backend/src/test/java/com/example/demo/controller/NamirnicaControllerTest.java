package com.example.demo.controller;

import com.example.demo.model.Namirnica;
import com.example.demo.repository.NamirnicaRepository;
import com.example.demo.repository.UnosNamirniceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class NamirnicaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private NamirnicaRepository namirnicaRepo;
    @MockBean private UnosNamirniceRepository unosRepo;

    private Namirnica makeNamirnica(Long id, String naziv, int kalorije) {
        Namirnica n = new Namirnica();
        n.setId(id);
        n.setNaziv(naziv);
        n.setKalorije(kalorije);
        n.setProteini(BigDecimal.valueOf(1.1));
        n.setUgljikohidrati(BigDecimal.valueOf(2.2));
        n.setMasti(BigDecimal.valueOf(3.3));
        return n;
    }

    @Test @DisplayName("GET /api/namirnice – returns all")
    void getAll_success() throws Exception {
        Namirnica n = makeNamirnica(1L, "Banana", 100);
        when(namirnicaRepo.findAll()).thenReturn(List.of(n));

        mockMvc.perform(get("/api/namirnice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].naziv").value("Banana"));
    }

    @Test @DisplayName("GET /api/namirnice/{id} – found")
    void getById_found() throws Exception {
        Namirnica n = makeNamirnica(5L, "Jabuka", 50);
        when(namirnicaRepo.findById(5L)).thenReturn(Optional.of(n));

        mockMvc.perform(get("/api/namirnice/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.naziv").value("Jabuka"));
    }

    @Test @DisplayName("GET /api/namirnice/{id} – not found")
    void getById_notFound() throws Exception {
        when(namirnicaRepo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/namirnice/99"))
            .andExpect(status().isNotFound());
    }

    @Test @DisplayName("POST /api/namirnice – create new")
    void create_success() throws Exception {
        Namirnica toCreate = makeNamirnica(null, "Kruska", 57);
        toCreate.setId(null);
        Namirnica saved = makeNamirnica(10L, "Kruska", 57);

        when(namirnicaRepo.save(any(Namirnica.class))).thenReturn(saved);

        mockMvc.perform(post("/api/namirnice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.naziv").value("Kruska"));
    }

    @Test @DisplayName("PUT /api/namirnice/{id} – update when not in use")
    void update_success() throws Exception {
        Namirnica existing = makeNamirnica(3L, "Staro", 10);
        Namirnica details  = makeNamirnica(null, "Novo", 20);

        when(namirnicaRepo.findById(3L)).thenReturn(Optional.of(existing));
        when(unosRepo.existsByNamirnica(existing)).thenReturn(false);
        when(namirnicaRepo.save(any(Namirnica.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(put("/api/namirnice/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.naziv").value("Novo"))
            .andExpect(jsonPath("$.kalorije").value(20));
    }

    @Test @DisplayName("PUT /api/namirnice/{id} – fail when in use")
    void update_inUse() throws Exception {
        Namirnica existing = makeNamirnica(7L, "X", 0);
        when(namirnicaRepo.findById(7L)).thenReturn(Optional.of(existing));
        when(unosRepo.existsByNamirnica(existing)).thenReturn(true);

        mockMvc.perform(put("/api/namirnice/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"naziv\":\"Anything\"}"))
            .andExpect(status().isForbidden());
    }

    @Test @DisplayName("DELETE /api/namirnice/{id} – delete when not in use")
    void delete_success() throws Exception {
        Namirnica existing = makeNamirnica(4L, "Y", 0);
        when(namirnicaRepo.findById(4L)).thenReturn(Optional.of(existing));
        when(unosRepo.existsByNamirnica(existing)).thenReturn(false);

        mockMvc.perform(delete("/api/namirnice/4"))
            .andExpect(status().isNoContent());

        verify(namirnicaRepo).delete(existing);
    }

    @Test @DisplayName("DELETE /api/namirnice/{id} – fail when in use")
    void delete_inUse() throws Exception {
        Namirnica existing = makeNamirnica(8L, "Z", 0);
        when(namirnicaRepo.findById(8L)).thenReturn(Optional.of(existing));
        when(unosRepo.existsByNamirnica(existing)).thenReturn(true);

        mockMvc.perform(delete("/api/namirnice/8"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/namirnice – fail if naziv already exists")
    void create_duplicateNaziv() throws Exception {
        Namirnica toCreate = makeNamirnica(null, "Banana", 50);
        toCreate.setId(null);

        when(namirnicaRepo.existsByNaziv("Banana")).thenReturn(true);

        mockMvc.perform(post("/api/namirnice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toCreate)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/namirnice/{id} – fail when updating to existing name")
    void update_duplicateNaziv() throws Exception {
        Namirnica existing = makeNamirnica(5L, "Staro", 10);
        Namirnica details  = makeNamirnica(null, "Banana", 20);

        when(namirnicaRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(unosRepo.existsByNamirnica(existing)).thenReturn(false);
        when(namirnicaRepo.existsByNaziv("Banana")).thenReturn(true);

        mockMvc.perform(put("/api/namirnice/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(details)))
            .andExpect(status().isForbidden()); 
    }

}

