package com.example.demo.repository;

import com.example.demo.model.Korisnik;
import com.example.demo.model.PrehrambeniDnevnik;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PrehrambeniDnevnikRepositoryTest {

    @Autowired
    private PrehrambeniDnevnikRepository dnevnikRepo;

    @Autowired
    private KorisnikRepository korisnikRepo;

    private Korisnik korisnik;
    private LocalDate danas;

    @BeforeEach
    void setUp() {
        // očisti sve
        dnevnikRepo.deleteAll();
        korisnikRepo.deleteAll();

        // spoji jednog korisnika
        korisnik = new Korisnik();
        korisnik.setIme("Test");
        korisnik.setPrezime("User");
        korisnik.setEmail("test@example.com");
        korisnik.setLozinka("pwd");
        korisnik.setSpol("M");
        korisnik.setDob(25);
        korisnik.setDatumRegistracije(LocalDate.now());
        korisnik = korisnikRepo.save(korisnik);

        danas = LocalDate.of(2025, 5, 25);
    }

    @Test
    @DisplayName("findByKorisnikAndDatumObroka vraća sve dnevnike za dan")
    void testFindByKorisnikAndDatumObroka() {
        // dva dnevnika za isti dan, različite vrste obroka
        PrehrambeniDnevnik d1 = new PrehrambeniDnevnik();
        d1.setKorisnik(korisnik);
        d1.setDatumObroka(danas);
        d1.setVrstaObroka("Doručak");
        dnevnikRepo.save(d1);

        PrehrambeniDnevnik d2 = new PrehrambeniDnevnik();
        d2.setKorisnik(korisnik);
        d2.setDatumObroka(danas);
        d2.setVrstaObroka("Ručak");
        dnevnikRepo.save(d2);

        // dnevnik za drugi datum
        PrehrambeniDnevnik d3 = new PrehrambeniDnevnik();
        d3.setKorisnik(korisnik);
        d3.setDatumObroka(danas.minusDays(1));
        d3.setVrstaObroka("Večera");
        dnevnikRepo.save(d3);

        List<PrehrambeniDnevnik> list = dnevnikRepo.findByKorisnikAndDatumObroka(korisnik, danas);
        assertThat(list)
            .hasSize(2)
            .extracting(PrehrambeniDnevnik::getVrstaObroka)
            .containsExactlyInAnyOrder("Doručak", "Ručak");
    }

    @Test
    @DisplayName("findByKorisnikAndDatumObrokaAndVrstaObroka vraća točno jedan dnevnik")
    void testFindByKorisnikAndDatumObrokaAndVrstaObroka() {
        PrehrambeniDnevnik dorucak = new PrehrambeniDnevnik();
        dorucak.setKorisnik(korisnik);
        dorucak.setDatumObroka(danas);
        dorucak.setVrstaObroka("Doručak");
        dnevnikRepo.save(dorucak);

        PrehrambeniDnevnik rucak = new PrehrambeniDnevnik();
        rucak.setKorisnik(korisnik);
        rucak.setDatumObroka(danas);
        rucak.setVrstaObroka("Ručak");
        dnevnikRepo.save(rucak);

        PrehrambeniDnevnik found = dnevnikRepo
            .findByKorisnikAndDatumObrokaAndVrstaObroka(korisnik, danas, "Ručak");

        assertThat(found).isNotNull();
        assertThat(found.getVrstaObroka()).isEqualTo("Ručak");
        assertThat(found.getDatumObroka()).isEqualTo(danas);
        assertThat(found.getKorisnik().getId()).isEqualTo(korisnik.getId());
    }

    @Test
    @DisplayName("kad nema unosa, obe metode vraćaju prazno ili null")
    void testEmptyResults() {
        List<PrehrambeniDnevnik> prazno = dnevnikRepo.findByKorisnikAndDatumObroka(korisnik, danas);
        assertThat(prazno).isEmpty();

        PrehrambeniDnevnik none = dnevnikRepo
            .findByKorisnikAndDatumObrokaAndVrstaObroka(korisnik, danas, "NePostoji");
        assertThat(none).isNull();
    }
}

