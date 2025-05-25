package com.example.demo.controller;

import com.example.demo.model.Klijent;
import com.example.demo.model.KlijentTrener;
import com.example.demo.model.KlijentTrenerId;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.KlijentTrenerRepository;
import com.example.demo.repository.TrenerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class KlijentTrenerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private KlijentTrenerRepository repo;
    @MockBean private KlijentRepository klijentRepo;
    @MockBean private TrenerRepository trenerRepo;

    private Klijent makeKlijent(Long id) {
        Klijent k = new Klijent();
        k.setId(id);
        return k;
    }

    private Trener makeTrener(Long id) {
        Trener t = new Trener();
        t.setId(id);
        return t;
    }

    private KlijentTrener makeVeza(Long klijentId, Long trenerId) {
        KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);
        KlijentTrener kt = new KlijentTrener();
        kt.setId(id);
        kt.setKlijent(makeKlijent(klijentId));
        kt.setTrener(makeTrener(trenerId));
        kt.setDatumPovezivanja(LocalDate.of(2025,5,25));
        kt.setStatus("Čeka");
        return kt;
    }

    @Test
    @DisplayName("POST /api/klijent-trener – povezi success")
    void povezi_success() throws Exception {
        Klijent k = makeKlijent(1L);
        Trener t = makeTrener(2L);
        when(klijentRepo.findById(1L)).thenReturn(Optional.of(k));
        when(trenerRepo.findById(2L)).thenReturn(Optional.of(t));
        KlijentTrener saved = makeVeza(1L,2L);
        when(repo.save(any(KlijentTrener.class))).thenReturn(saved);

        Map<String,Object> body = Map.of("klijentId",1,"trenerId",2);
        mockMvc.perform(post("/api/klijent-trener")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id.klijentId").value(1))
            .andExpect(jsonPath("$.id.trenerId").value(2))
            .andExpect(jsonPath("$.status").value("Čeka"));

        verify(repo).save(any(KlijentTrener.class));
    }

    @Test
    @DisplayName("GET /api/klijent-trener/moji – returns list for klijent")
    void moji_success() throws Exception {
        KlijentTrener one = makeVeza(1L,2L);
        when(repo.findByKlijentId(1L)).thenReturn(List.of(one));

        mockMvc.perform(get("/api/klijent-trener/moji")
                .param("klijentId","1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id.klijentId").value(1));
    }

    @Test
    @DisplayName("GET /api/klijent-trener/moji-klijenti – returns list for trener")
    void mojiKlijenti_success() throws Exception {
        KlijentTrener one = makeVeza(1L,2L);
        when(repo.findByTrenerId(2L)).thenReturn(List.of(one));

        mockMvc.perform(get("/api/klijent-trener/moji-klijenti")
                .param("trenerId","2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id.trenerId").value(2));
    }

    @Test
    @DisplayName("PATCH /api/klijent-trener/potvrdi – odbijeno deletes")
    void potvrdi_rejected() throws Exception {
        Map<String,Object> body = Map.of("klijentId",1,"trenerId",2,"status","Odbijeno");

        mockMvc.perform(patch("/api/klijent-trener/potvrdi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(repo).deleteById(new KlijentTrenerId(1L,2L));
    }

    @Test
    @DisplayName("PATCH /api/klijent-trener/potvrdi – prihvaceno updates")
    void potvrdi_accepted() throws Exception {
        KlijentTrener existing = makeVeza(1L,2L);
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repo.save(any(KlijentTrener.class))).thenAnswer(i -> i.getArgument(0));

        Map<String,Object> body = Map.of("klijentId",1,"trenerId",2,"status","Prihvaćeno");
        mockMvc.perform(patch("/api/klijent-trener/potvrdi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("Prihvaćeno"));

        verify(repo).save(any(KlijentTrener.class));
    }

    @Test
    @DisplayName("DELETE /api/klijent-trener – prekini odnos")
    void prekini_success() throws Exception {
        mockMvc.perform(delete("/api/klijent-trener")
                .param("klijentId","1")
                .param("trenerId","2"))
            .andExpect(status().isOk());

        verify(repo).deleteById(new KlijentTrenerId(1L,2L));
    }
}

