package com.example.demo.repository;

import com.example.demo.model.Aktivnost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class AktivnostRepositoryTest {

    @Autowired
    private AktivnostRepository aktivnostRepo;

    @Test
    @DisplayName("existsByVrstaAktivnosti returns false if no Aktivnost saved")
    void existsByVrstaAktivnosti_falseWhenEmpty() {
        boolean exists = aktivnostRepo.existsByVrstaAktivnosti("Trčanje");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByVrstaAktivnosti returns true after saving Aktivnost with that vrsta")
    void existsByVrstaAktivnosti_trueAfterSave() {
        // given
        Aktivnost a = new Aktivnost();
        a.setVrstaAktivnosti("Biciklizam");
        a.setKalorijePoSatu(500.0);
        aktivnostRepo.save(a);

        // when
        boolean exists = aktivnostRepo.existsByVrstaAktivnosti("Biciklizam");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("standard CRUD operations work")
    void crudOperations() {
        // create
        Aktivnost a = new Aktivnost();
        a.setVrstaAktivnosti("Plivanje");
        a.setKalorijePoSatu(700.0);
        Aktivnost saved = aktivnostRepo.save(a);
        assertThat(saved.getId()).isNotNull();

        // read
        Aktivnost found = aktivnostRepo.findById(saved.getId()).orElseThrow();
        assertThat(found.getVrstaAktivnosti()).isEqualTo("Plivanje");

        // update
        found.setKalorijePoSatu(750.0);
        aktivnostRepo.save(found);
        Aktivnost updated = aktivnostRepo.findById(saved.getId()).orElseThrow();
        assertThat(updated.getKalorijePoSatu()).isEqualTo(750.0);

        // delete
        aktivnostRepo.delete(updated);
        assertThat(aktivnostRepo.findById(saved.getId())).isEmpty();
    }
}

