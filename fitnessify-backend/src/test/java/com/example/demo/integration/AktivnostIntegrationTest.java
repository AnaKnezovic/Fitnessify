package com.example.demo.integration;

import com.example.demo.model.Aktivnost;
import com.example.demo.model.Korisnik;
import com.example.demo.model.KorisnikAktivnost;
import com.example.demo.repository.AktivnostRepository;
import com.example.demo.repository.KorisnikAktivnostRepository;
import com.example.demo.repository.KorisnikRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc(addFilters = false)          // disable security
@AutoConfigureTestDatabase                        // use in-memory DB
@ActiveProfiles("test")
class AktivnostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KorisnikRepository korisnikRepo;

    @Autowired
    private AktivnostRepository aktivnostRepo;

    @Autowired
    private KorisnikAktivnostRepository korisnikAktRepo;

    @BeforeEach
    void clean() {
        korisnikAktRepo.deleteAll();
        aktivnostRepo.deleteAll();
        korisnikRepo.deleteAll();
    }

    @Test
    void addAndFetchKorisnikAktivnost() throws Exception {
        // --- prepare one user and one activity ---
        Korisnik k = new Korisnik();
        k.setIme("Test");
        k.setPrezime("User");
        k.setEmail("test@example.com");
        k.setLozinka("pw");
        k.setSpol("M");
        k.setDob(25);
        k.setDatumRegistracije(LocalDate.now());
        k = korisnikRepo.save(k);

        Aktivnost a = new Aktivnost();
        a.setVrstaAktivnosti("Trčanje");
        a.setKalorijePoSatu(600.0);
        a = aktivnostRepo.save(a);

        LocalDate today = LocalDate.now();
        String body = String.format("""
            {
              "korisnikId": %d,
              "aktivnostId": %d,
              "trajanje": 30,
              "datum": "%s"
            }
            """, k.getId(), a.getId(), today);

        // --- call your endpoint ---
        mockMvc.perform(post("/api/aktivnosti/korisnik")
                .contentType("application/json")
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.potroseneKalorije").value(600.0 * 30 / 60));

        // --- assert persisted to the DB ---
        List<KorisnikAktivnost> all = korisnikAktRepo.findAll();
        assertThat(all, hasSize(1));
        KorisnikAktivnost ka = all.get(0);
        assertThat(ka.getId().getKorisnikId(), is(k.getId()));
        assertThat(ka.getId().getAktivnostId(), is(a.getId()));
        assertThat(ka.getId().getDatumAktivnosti(), is(today));
        assertThat(ka.getPotroseneKalorije(), is(closeTo(600.0 * 30 / 60, 1e-6)));
    }
}
