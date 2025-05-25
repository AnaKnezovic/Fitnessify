package com.example.demo.repository;

import com.example.demo.model.Aktivnost;
import com.example.demo.model.Korisnik;
import com.example.demo.model.KorisnikAktivnost;
import com.example.demo.model.KorisnikAktivnostId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KorisnikAktivnostRepositoryTest {

    @Autowired
    private KorisnikAktivnostRepository repo;

    @Autowired
    private KorisnikRepository korisnikRepo;

    @Autowired
    private AktivnostRepository aktivnostRepo;

    private Korisnik korisnik;
    private Aktivnost aktivnost1, aktivnost2;
    private LocalDate danas, sutra;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        aktivnostRepo.deleteAll();
        korisnikRepo.deleteAll();

        // spremi jednog korisnika
        korisnik = new Korisnik();
        korisnik.setEmail("test@example.com");
        korisnik = korisnikRepo.save(korisnik);

        // spremi dvije aktivnosti
        aktivnost1 = new Aktivnost();
        aktivnost1.setVrstaAktivnosti("A1");
        aktivnost1.setKalorijePoSatu(600.0);
        aktivnost1 = aktivnostRepo.save(aktivnost1);

        aktivnost2 = new Aktivnost();
        aktivnost2.setVrstaAktivnosti("A2");
        aktivnost2.setKalorijePoSatu(300.0);
        aktivnost2 = aktivnostRepo.save(aktivnost2);

        danas = LocalDate.now();
        sutra = danas.plusDays(1);
    }

    private KorisnikAktivnost saveRecord(Aktivnost a, LocalDate datum, int trajanje, double potrosnja) {
        KorisnikAktivnost ka = new KorisnikAktivnost();
        KorisnikAktivnostId id = new KorisnikAktivnostId(korisnik.getId(), a.getId(), datum);
        ka.setId(id);
        ka.setKorisnik(korisnik);
        ka.setAktivnost(a);
        ka.setTrajanje(trajanje);
        ka.setPotroseneKalorije(potrosnja);
        return repo.save(ka);
    }

    @Test
    @DisplayName("findByKorisnikIdAndIdDatumAktivnosti vraća točno zapise za dan")
    void testFindByKorisnikIdAndIdDatumAktivnosti() {
        saveRecord(aktivnost1, danas, 30, 300.0);
        saveRecord(aktivnost2, sutra, 20, 100.0);

        List<KorisnikAktivnost> list = repo.findByKorisnikIdAndIdDatumAktivnosti(korisnik.getId(), danas);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId().getDatumAktivnosti()).isEqualTo(danas);
    }

    @Test
    @DisplayName("findByKorisnikId vraća sve zapise za korisnika")
    void testFindByKorisnikId() {
        saveRecord(aktivnost1, danas, 30, 300.0);
        saveRecord(aktivnost2, sutra, 20, 100.0);

        List<KorisnikAktivnost> list = repo.findByKorisnikId(korisnik.getId());
        assertThat(list).hasSize(2)
            .extracting(ka -> ka.getAktivnost().getId())
            .containsExactlyInAnyOrder(aktivnost1.getId(), aktivnost2.getId());
    }

    @Test
    @DisplayName("existsByAktivnost prepoznaje postojeću aktivnost u zapisima")
    void testExistsByAktivnost() {
        // prije spremanja nema zapisa
        assertThat(repo.existsByAktivnost(aktivnost1)).isFalse();

        saveRecord(aktivnost1, danas, 10, 100.0);

        assertThat(repo.existsByAktivnost(aktivnost1)).isTrue();
        assertThat(repo.existsByAktivnost(aktivnost2)).isFalse();
    }

    @Test
    @DisplayName("ukupnoPotroseneKalorijeZaDatum ispravno zbraja kalorije")
    void testUkupnoPotroseneKalorijeZaDatum() {
        saveRecord(aktivnost1, danas, 30, 300.0);
        saveRecord(aktivnost2, danas, 20, 100.0);
        saveRecord(aktivnost1, sutra, 60, 600.0);

        Double sumToday = repo.ukupnoPotroseneKalorijeZaDatum(korisnik.getId(), danas);
        Double sumTomorrow = repo.ukupnoPotroseneKalorijeZaDatum(korisnik.getId(), sutra);
        Double sumNone = repo.ukupnoPotroseneKalorijeZaDatum(999L, danas);

        assertThat(sumToday).isEqualTo(400.0);
        assertThat(sumTomorrow).isEqualTo(600.0);
        assertThat(sumNone).isEqualTo(0.0);
    }
}

