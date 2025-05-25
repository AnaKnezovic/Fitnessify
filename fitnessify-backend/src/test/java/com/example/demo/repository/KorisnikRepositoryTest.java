package com.example.demo.repository;

import com.example.demo.model.Korisnik;
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
class KorisnikRepositoryTest {

    @Autowired
    private KorisnikRepository repo;

    private Korisnik k1, k2;

    @BeforeEach
    void setUp() {
        repo.deleteAll();

        k1 = new Korisnik();
        k1.setIme("Marko");
        k1.setPrezime("Marković");
        k1.setEmail("marko@example.com");
        k1.setLozinka("lozinka");
        k1.setSpol("M");
        k1.setDob(25);
        k1.setDatumRegistracije(LocalDate.of(2025, 1, 1));

        k2 = new Korisnik();
        k2.setIme("Ivana");
        k2.setPrezime("Ivić");
        k2.setEmail("ivana@example.com");
        k2.setLozinka("tajna");
        k2.setSpol("Ž");
        k2.setDob(30);
        k2.setDatumRegistracije(LocalDate.of(2025, 2, 1));

        // spremi oba korisnika
        repo.save(k1);
        repo.save(k2);
    }

    @Test
    @DisplayName("findAll vraća sve spremljene korisnike")
    void testFindAll() {
        List<Korisnik> svi = repo.findAll();
        assertThat(svi).hasSize(2)
            .extracting(Korisnik::getEmail)
            .containsExactlyInAnyOrder("marko@example.com", "ivana@example.com");
    }

    @Test
    @DisplayName("findById vraća Optional sa entitetom ako postoji")
    void testFindById() {
        Optional<Korisnik> o = repo.findById(k1.getId());
        assertThat(o).isPresent();
        assertThat(o.get().getEmail()).isEqualTo("marko@example.com");
    }

    @Test
    @DisplayName("findById vraća empty ako ne postoji")
    void testFindByIdNotFound() {
        Optional<Korisnik> o = repo.findById(999L);
        assertThat(o).isNotPresent();
    }

    @Test
    @DisplayName("findByEmail vraća Optional sa korisnikom ako email postoji")
    void testFindByEmailFound() {
        Optional<Korisnik> o = repo.findByEmail("ivana@example.com");
        assertThat(o).isPresent();
        assertThat(o.get().getPrezime()).isEqualTo("Ivić");
    }

    @Test
    @DisplayName("findByEmail vraća empty ako email ne postoji")
    void testFindByEmailNotFound() {
        Optional<Korisnik> o = repo.findByEmail("noone@nowhere");
        assertThat(o).isNotPresent();
    }

    @Test
    @DisplayName("deleteById briše korisnika iz repozitorija")
    void testDeleteById() {
        long id = k1.getId();
        repo.deleteById(id);
        assertThat(repo.findById(id)).isNotPresent();
        // ostao samo jedan
        assertThat(repo.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("save može ažurirati postojeći entitet")
    void testSaveUpdate() {
        Korisnik m = repo.findByEmail("marko@example.com").get();
        m.setPrezime("Novak");
        repo.save(m);

        Korisnik updated = repo.findById(m.getId()).get();
        assertThat(updated.getPrezime()).isEqualTo("Novak");
    }
}

