package com.example.demo.repository;

import com.example.demo.model.*;
import com.example.demo.model.UnosNamirnice.UnosNamirniceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UnosNamirniceRepositoryTest {

    @Autowired
    private UnosNamirniceRepository unosRepo;

    @Autowired
    private PrehrambeniDnevnikRepository dnevnikRepo;

    @Autowired
    private KorisnikRepository korisnikRepo;

    @Autowired
    private NamirnicaRepository namirnicaRepo;

    private Korisnik korisnik;
    private PrehrambeniDnevnik dnevnik;
    private Namirnica banana;
    private Namirnica jabuka;
    private LocalDate danas;

    @BeforeEach
    void setUp() {
        // očisti sve
        unosRepo.deleteAll();
        dnevnikRepo.deleteAll();
        korisnikRepo.deleteAll();
        namirnicaRepo.deleteAll();

        // stvori korisnika
        korisnik = new Korisnik();
        korisnik.setIme("Test");
        korisnik.setPrezime("User");
        korisnik.setEmail("test@ex.com");
        korisnik.setLozinka("pwd");
        korisnik.setSpol("M");
        korisnik.setDob(30);
        korisnik.setDatumRegistracije(LocalDate.now());
        korisnik = korisnikRepo.save(korisnik);

        // stvori dnevnik
        danas = LocalDate.of(2025,5,25);
        dnevnik = new PrehrambeniDnevnik();
        dnevnik.setKorisnik(korisnik);
        dnevnik.setDatumObroka(danas);
        dnevnik.setVrstaObroka("Ručak");
        dnevnik = dnevnikRepo.save(dnevnik);

        // stvori dvije namirnice
        banana = new Namirnica();
        banana.setNaziv("Banana");
        banana.setKalorije(100);
        banana.setProteini(BigDecimal.valueOf(1.1));
        banana.setUgljikohidrati(BigDecimal.valueOf(23.0));
        banana.setMasti(BigDecimal.valueOf(0.3));
        banana = namirnicaRepo.save(banana);

        jabuka = new Namirnica();
        jabuka.setNaziv("Jabuka");
        jabuka.setKalorije(52);
        jabuka.setProteini(BigDecimal.valueOf(0.3));
        jabuka.setUgljikohidrati(BigDecimal.valueOf(14.0));
        jabuka.setMasti(BigDecimal.valueOf(0.2));
        jabuka = namirnicaRepo.save(jabuka);

        // umetni dva unosa
        UnosNamirnice u1 = new UnosNamirnice();
        u1.setDnevnik(dnevnik);
        u1.setNamirnica(banana);
        u1.setKolicina(BigDecimal.valueOf(150)); // 150g banana → 150*100/100 = 150 kcal
        unosRepo.save(u1);

        UnosNamirnice u2 = new UnosNamirnice();
        u2.setDnevnik(dnevnik);
        u2.setNamirnica(jabuka);
        u2.setKolicina(BigDecimal.valueOf(200)); // 200g jabuka → 200*52/100 = 104 kcal
        unosRepo.save(u2);
    }

    @Test
    @DisplayName("findByDnevnik vraća sve unose za dati dnevnik")
    void testFindByDnevnik() {
        List<UnosNamirnice> list = unosRepo.findByDnevnik(dnevnik);
        assertThat(list)
            .hasSize(2)
            .extracting(u -> u.getNamirnica().getNaziv())
            .containsExactlyInAnyOrder("Banana", "Jabuka");
    }

    @Test
    @DisplayName("findByDnevnikAndNamirnica vraća točno jedan unos")
    void testFindByDnevnikAndNamirnica() {
        UnosNamirnice bananaUnos = unosRepo.findByDnevnikAndNamirnica(dnevnik, banana);
        assertThat(bananaUnos).isNotNull();
        assertThat(bananaUnos.getKolicina()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    @DisplayName("existsByNamirnica vraća true za postojeću namirnicu")
    void testExistsByNamirnica() {
        assertThat(unosRepo.existsByNamirnica(banana)).isTrue();
        // ne postoji unos za npr. neku novu namirnicu
        Namirnica kruh = new Namirnica();
        kruh.setNaziv("Kruh");
        kruh.setKalorije(265);
        kruh = namirnicaRepo.save(kruh);
        assertThat(unosRepo.existsByNamirnica(kruh)).isFalse();
    }

    @Test
    @DisplayName("ukupnoKalorijaZaDatum računa sumu kalorija svih unosa")
    void testUkupnoKalorijaZaDatum() {
        // banana:150 kcal + jabuka:104 kcal = 254.0
        Double total = unosRepo.ukupnoKalorijaZaDatum(korisnik.getId(), danas);
        assertThat(total).isEqualTo(254.0);
    }
}

