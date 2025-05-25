package com.example.demo.repository;

import com.example.demo.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KlijentTrenerRepositoryTest {

    @Autowired
    private KlijentTrenerRepository repo;
    @Autowired
    private KorisnikRepository korisnikRepo;
    @Autowired
    private KlijentRepository klijentRepo;
    @Autowired
    private TrenerRepository trenerRepo;

    private Klijent createAndSaveKlijent(String email) {
        // kreiramo i spremamo Korisnik
        Korisnik k = new Korisnik();
        k.setEmail(email);
        korisnikRepo.save(k);
        // kreiramo i spremamo Klijent
        Klijent kl = new Klijent();
        kl.setKorisnik(k);
        return klijentRepo.save(kl);
    }

    private Trener createAndSaveTrener(String email) {
        Korisnik t = new Korisnik();
        t.setEmail(email);
        korisnikRepo.save(t);
        Trener tr = new Trener();
        tr.setKorisnik(t);
        return trenerRepo.save(tr);
    }

    @Test
    @DisplayName("findByKlijentId vraća samo veze za tog klijenta")
    void findByKlijentId() {
        // kreiramo jednog klijenta i dva trenera
        Klijent kl1 = createAndSaveKlijent("k1@example.com");
        Trener tr1 = createAndSaveTrener("t1@example.com");
        Trener tr2 = createAndSaveTrener("t2@example.com");

        // dvije veze za kl1
        KlijentTrener veza1 = new KlijentTrener();
        veza1.setId(new KlijentTrenerId(kl1.getId(), tr1.getId()));
        veza1.setKlijent(kl1);
        veza1.setTrener(tr1);
        veza1.setDatumPovezivanja(LocalDate.now());
        veza1.setStatus("Čeka");
        repo.save(veza1);

        KlijentTrener veza2 = new KlijentTrener();
        veza2.setId(new KlijentTrenerId(kl1.getId(), tr2.getId()));
        veza2.setKlijent(kl1);
        veza2.setTrener(tr2);
        veza2.setDatumPovezivanja(LocalDate.now());
        veza2.setStatus("Čeka");
        repo.save(veza2);

        // drugačiji klijent
        Klijent kl2 = createAndSaveKlijent("k2@example.com");
        KlijentTrener veza3 = new KlijentTrener();
        veza3.setId(new KlijentTrenerId(kl2.getId(), tr1.getId()));
        veza3.setKlijent(kl2);
        veza3.setTrener(tr1);
        veza3.setDatumPovezivanja(LocalDate.now());
        veza3.setStatus("Čeka");
        repo.save(veza3);

        // testiramo
        List<KlijentTrener> result = repo.findByKlijentId(kl1.getId());
        assertThat(result).hasSize(2)
            .allMatch(kt -> kt.getKlijent().getId().equals(kl1.getId()));
    }

    @Test
    @DisplayName("findByTrenerId vraća samo veze za tog trenera")
    void findByTrenerId() {
        // kreiramo dva klijenta i jednog trenera
        Klijent kl1 = createAndSaveKlijent("c1@example.com");
        Klijent kl2 = createAndSaveKlijent("c2@example.com");
        Trener tr = createAndSaveTrener("t@example.com");

        // dvije veze za tr
        KlijentTrener veza1 = new KlijentTrener();
        veza1.setId(new KlijentTrenerId(kl1.getId(), tr.getId()));
        veza1.setKlijent(kl1);
        veza1.setTrener(tr);
        veza1.setDatumPovezivanja(LocalDate.now());
        veza1.setStatus("Čeka");
        repo.save(veza1);

        KlijentTrener veza2 = new KlijentTrener();
        veza2.setId(new KlijentTrenerId(kl2.getId(), tr.getId()));
        veza2.setKlijent(kl2);
        veza2.setTrener(tr);
        veza2.setDatumPovezivanja(LocalDate.now());
        veza2.setStatus("Čeka");
        repo.save(veza2);

        // drugi trener
        Trener tr2 = createAndSaveTrener("t2@example.com");
        KlijentTrener veza3 = new KlijentTrener();
        veza3.setId(new KlijentTrenerId(kl1.getId(), tr2.getId()));
        veza3.setKlijent(kl1);
        veza3.setTrener(tr2);
        veza3.setDatumPovezivanja(LocalDate.now());
        veza3.setStatus("Čeka");
        repo.save(veza3);

        // testiramo
        List<KlijentTrener> result = repo.findByTrenerId(tr.getId());
        assertThat(result).hasSize(2)
            .allMatch(kt -> kt.getTrener().getId().equals(tr.getId()));
    }
}
