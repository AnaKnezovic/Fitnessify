package com.example.demo.controller;

import com.example.demo.dto.NoviDnevnikDTO;
import com.example.demo.dto.UnosNamirniceDTO;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Namirnica;
import com.example.demo.model.PrehrambeniDnevnik;
import com.example.demo.model.UnosNamirnice;
import com.example.demo.repository.KorisnikRepository;
import com.example.demo.repository.NamirnicaRepository;
import com.example.demo.repository.PrehrambeniDnevnikRepository;
import com.example.demo.repository.UnosNamirniceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PrehrambeniDnevnikControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrehrambeniDnevnikRepository dnevnikRepo;
    @MockBean
    private KorisnikRepository korisnikRepo;
    @MockBean
    private NamirnicaRepository namirnicaRepo;
    @MockBean
    private UnosNamirniceRepository unosRepo;

    @Test
    @DisplayName("POST /api/dnevnik – create new dnevnik and entries")
    void addDnevnik_success() throws Exception {
        NoviDnevnikDTO dto = new NoviDnevnikDTO();
        dto.setKorisnikId(1L);
        dto.setDatumObroka(LocalDate.of(2025,5,24));
        dto.setVrstaObroka("RUCak");
        UnosNamirniceDTO uDto = new UnosNamirniceDTO();
        uDto.setNamirnicaId(10L);
        uDto.setKolicina(BigDecimal.valueOf(150));
        dto.setNamirnice(List.of(uDto));

        Korisnik korisnik = new Korisnik(); korisnik.setId(1L);
        when(korisnikRepo.findById(1L)).thenReturn(Optional.of(korisnik));
        when(dnevnikRepo.findByKorisnikAndDatumObrokaAndVrstaObroka(eq(korisnik), any(), eq("RUCak")))
            .thenReturn(null);
        PrehrambeniDnevnik dnevnik = new PrehrambeniDnevnik();
        dnevnik.setId(5L);
        dnevnik.setKorisnik(korisnik);
        dnevnik.setDatumObroka(dto.getDatumObroka());
        dnevnik.setVrstaObroka("RUCak");
        when(dnevnikRepo.save(any(PrehrambeniDnevnik.class))).thenReturn(dnevnik);

        Namirnica nam = new Namirnica(); nam.setId(10L);
        when(namirnicaRepo.findById(10L)).thenReturn(Optional.of(nam));
        when(unosRepo.findByDnevnikAndNamirnica(dnevnik, nam)).thenReturn(null);
        when(unosRepo.save(any(UnosNamirnice.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/dnevnik")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.vrstaObroka").value("RUCak"));
    }

    @Test
    @DisplayName("GET /api/dnevnik/{korisnikId}/{datum} – return daily diaries")
    void getDnevnikZaDan_success() throws Exception {
        Korisnik korisnik = new Korisnik(); korisnik.setId(2L);
        when(korisnikRepo.findById(2L)).thenReturn(Optional.of(korisnik));
        PrehrambeniDnevnik d1 = new PrehrambeniDnevnik(); d1.setId(7L);
        when(dnevnikRepo.findByKorisnikAndDatumObroka(eq(korisnik), eq(LocalDate.of(2025,5,24))))
            .thenReturn(List.of(d1));

        mockMvc.perform(get("/api/dnevnik/2/2025-05-24"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(7));
    }

    @Test
    @DisplayName("GET /api/dnevnik/unosi/{dnevnikId} – return entries for a diary")
    void getUnosiZaDnevnik_success() throws Exception {
        PrehrambeniDnevnik d = new PrehrambeniDnevnik(); d.setId(3L);
        when(dnevnikRepo.findById(3L)).thenReturn(Optional.of(d));
        UnosNamirnice u = new UnosNamirnice();
        u.setDnevnik(d);
        Namirnica nam = new Namirnica(); nam.setId(20L);
        u.setNamirnica(nam);
        u.setKolicina(BigDecimal.valueOf(100));
        when(unosRepo.findByDnevnik(d)).thenReturn(List.of(u));

        mockMvc.perform(get("/api/dnevnik/unosi/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].namirnica.id").value(20))
            .andExpect(jsonPath("$[0].kolicina").value(100));
    }

    @Test
    @DisplayName("GET /api/dnevnik/svi-unosi/{korisnikId}/{datum} – return all entries for user and date")
    void getSviUnosiZaDan_success() throws Exception {
        Korisnik k = new Korisnik(); k.setId(4L);
        when(korisnikRepo.findById(4L)).thenReturn(Optional.of(k));
        PrehrambeniDnevnik dA = new PrehrambeniDnevnik(); dA.setId(8L);
        PrehrambeniDnevnik dB = new PrehrambeniDnevnik(); dB.setId(9L);
        when(dnevnikRepo.findByKorisnikAndDatumObroka(eq(k), eq(LocalDate.of(2025,5,24))))
            .thenReturn(List.of(dA, dB));
        UnosNamirnice u1 = new UnosNamirnice(); u1.setDnevnik(dA); u1.setKolicina(BigDecimal.valueOf(50));
        UnosNamirnice u2 = new UnosNamirnice(); u2.setDnevnik(dB); u2.setKolicina(BigDecimal.valueOf(75));
        when(unosRepo.findByDnevnik(dA)).thenReturn(List.of(u1));
        when(unosRepo.findByDnevnik(dB)).thenReturn(List.of(u2));

        mockMvc.perform(get("/api/dnevnik/svi-unosi/4/2025-05-24"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].kolicina").value(50))
            .andExpect(jsonPath("$[1].kolicina").value(75));
    }

    @Test
    @DisplayName("DELETE /api/dnevnik/unos/{dnevnikId}/{namirnicaId} – delete an entry")
    void deleteUnos_success() throws Exception {
        mockMvc.perform(delete("/api/dnevnik/unos/5/10"))
            .andExpect(status().isOk());

        verify(unosRepo).deleteById(argThat(id ->
            id.getDnevnik().equals(5L) && id.getNamirnica().equals(10L)
        ));
    }

    @Test
    @DisplayName("PUT /api/dnevnik/unos/{dnevnikId}/{namirnicaId} – edit an entry")
    void editUnos_success() throws Exception {
        UnosNamirnice u = new UnosNamirnice();
        UnosNamirnice.UnosNamirniceId key = new UnosNamirnice.UnosNamirniceId();
        key.setDnevnik(6L); key.setNamirnica(30L);
        u.setDnevnik(new PrehrambeniDnevnik()); u.getDnevnik().setId(6L);
        u.setNamirnica(new Namirnica()); u.getNamirnica().setId(30L);
        u.setKolicina(BigDecimal.valueOf(120));
        when(unosRepo.findById(any(UnosNamirnice.UnosNamirniceId.class)))
            .thenReturn(Optional.of(u));
        when(unosRepo.save(any(UnosNamirnice.class))).thenAnswer(inv -> inv.getArgument(0));

        UnosNamirnice update = new UnosNamirnice();
        update.setKolicina(BigDecimal.valueOf(200));

        mockMvc.perform(put("/api/dnevnik/unos/6/30")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.kolicina").value(200));
    }
}
