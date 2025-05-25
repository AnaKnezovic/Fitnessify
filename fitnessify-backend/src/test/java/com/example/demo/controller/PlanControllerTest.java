package com.example.demo.controller;

import com.example.demo.model.Klijent;
import com.example.demo.model.KlijentPlan;
import com.example.demo.model.KlijentPlanId;
import com.example.demo.model.Plan;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentPlanRepository;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.PlanRepository;
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
class PlanControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PlanRepository planRepo;
    @MockBean private KlijentPlanRepository klijentPlanRepo;
    @MockBean private KlijentRepository klijentRepo;
    @MockBean private TrenerRepository trenerRepo;

    private Trener makeTrener(Long id) {
        Trener t = new Trener();
        t.setId(id);
        return t;
    }

    private Klijent makeKlijent(Long id) {
        Klijent k = new Klijent();
        k.setId(id);
        return k;
    }

    private Plan makePlan(Long id, Long trenerId) {
        Plan p = new Plan();
        p.setId(id);
        p.setNazivPlana("Plan " + id);
        p.setOpis("Opis " + id);
        p.setTrener(makeTrener(trenerId));
        p.setDatumKreiranja(LocalDate.of(2025, 5, 25));
        return p;
    }

    private KlijentPlan makeKlijentPlan(Long klijentId, Long planId) {
        KlijentPlanId id = new KlijentPlanId(klijentId, planId);
        KlijentPlan kp = new KlijentPlan();
        kp.setId(id);
        kp.setKlijent(makeKlijent(klijentId));
        kp.setPlan(makePlan(planId, 9L));
        kp.setDatumDodjele(LocalDate.of(2025, 5, 25));
        kp.setStatus("Aktivan");
        return kp;
    }

    @Test
    @DisplayName("GET /api/planovi/{trenerId} – returns plans of trener")
    void treneroviPlanovi_success() throws Exception {
        Plan p = makePlan(1L, 42L);
        when(planRepo.findByTrenerId(42L)).thenReturn(List.of(p));

        mockMvc.perform(get("/api/planovi/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nazivPlana").value("Plan 1"));
    }

    @Test
    @DisplayName("POST /api/planovi/dodijeli – assigns plan to klijent")
    void dodijeliPlan_success() throws Exception {
        Klijent k = makeKlijent(7L);
        Plan p = makePlan(3L, 9L);
        when(klijentRepo.findById(7L)).thenReturn(Optional.of(k));
        when(planRepo.findById(3L)).thenReturn(Optional.of(p));
        KlijentPlan saved = makeKlijentPlan(7L, 3L);
        when(klijentPlanRepo.save(any(KlijentPlan.class))).thenReturn(saved);

        Map<String,Object> body = Map.of("klijentId",7,"planId",3);
        mockMvc.perform(post("/api/planovi/dodijeli")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id.klijentId").value(7))
            .andExpect(jsonPath("$.id.planId").value(3))
            .andExpect(jsonPath("$.status").value("Aktivan"));

        verify(klijentPlanRepo).save(any(KlijentPlan.class));
    }

    @Test
    @DisplayName("GET /api/planovi/moji – returns client's plans")
    void mojiPlanovi_success() throws Exception {
        KlijentPlan kp = makeKlijentPlan(5L, 2L);
        when(klijentPlanRepo.findByKlijentId(5L)).thenReturn(List.of(kp));

        mockMvc.perform(get("/api/planovi/moji")
                .param("klijentId","5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id.klijentId").value(5));
    }

     @Test
    @DisplayName("POST /api/planovi – create new plan")
    void kreirajPlan_success() throws Exception {
        Trener t = makeTrener(99L);
        when(trenerRepo.findById(99L)).thenReturn(Optional.of(t));

        Plan toSave = new Plan();
        toSave.setNazivPlana("NoviPlan");
        toSave.setOpis("Opis");
        toSave.setTrener(t);

        Plan saved = makePlan(10L, 99L);
        saved.setNazivPlana("NoviPlan");
        saved.setOpis("Opis");
        when(planRepo.save(any(Plan.class))).thenReturn(saved);

        Map<String,Object> body = Map.of(
            "nazivPlana","NoviPlan",
            "opis","Opis",
            "trenerId",99
        );
        mockMvc.perform(post("/api/planovi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            // id will still be null in the returned JSON:
            .andExpect(jsonPath("$.id").isEmpty())
            .andExpect(jsonPath("$.nazivPlana").value("NoviPlan"))
            .andExpect(jsonPath("$.opis").value("Opis"));

        verify(planRepo).save(any(Plan.class));
    }
}

