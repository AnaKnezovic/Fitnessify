package com.example.demo.controller;

import com.example.demo.model.Korisnik;
import com.example.demo.model.Trener;
import com.example.demo.repository.TrenerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TreneriControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrenerRepository trenerRepo;

    @Test
    @DisplayName("GET /api/treneri – returns list of all trainers")
    void getAll_success() throws Exception {
        // prepare two trainers
        Trener t1 = new Trener();
        t1.setId(1L);
        t1.setStrucnost("Fitness");
        t1.setGodineIskustva(5);
        t1.setKorisnik(null);

        Trener t2 = new Trener();
        t2.setId(2L);
        t2.setStrucnost("Yoga");
        t2.setGodineIskustva(3);
        t2.setKorisnik(null);

        when(trenerRepo.findAll()).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/treneri"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].strucnost").value("Fitness"))
            .andExpect(jsonPath("$[0].godineIskustva").value(5))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].strucnost").value("Yoga"))
            .andExpect(jsonPath("$[1].godineIskustva").value(3));
    }
}

