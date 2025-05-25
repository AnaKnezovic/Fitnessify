package com.example.demo.repository;

import com.example.demo.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class KlijentPlanRepositoryTest {

    @Autowired private KlijentPlanRepository klijentPlanRepo;
    @Autowired private KlijentRepository klijentRepo;
    @Autowired private PlanRepository planRepo;
    @Autowired private KorisnikRepository korisnikRepo;

    @Test
    @DisplayName("findByKlijentId returns empty when no assignments")
    void findByKlijentId_empty() {
        assertThat(klijentPlanRepo.findByKlijentId(999L)).isEmpty();
    }

    @Test
    @DisplayName("findByKlijentId returns only assignments for that client")
    void findByKlijentId_returnsOnlyThatKlijent() {
        Korisnik u1 = korisnikRepo.save(new KorisnikBuilder().email("u1@example.com").build());
        Klijent k1 = new Klijent();
        k1.setKorisnik(u1);
        klijentRepo.save(k1);

        Korisnik u2 = korisnikRepo.save(new KorisnikBuilder().email("u2@example.com").build());
        Klijent k2 = new Klijent();
        k2.setKorisnik(u2);
        klijentRepo.save(k2);

        Plan p1 = planRepo.save(new PlanBuilder().naziv("Plan A").build());
        Plan p2 = planRepo.save(new PlanBuilder().naziv("Plan B").build());

        KlijentPlan kp1 = new KlijentPlan();
        kp1.setId(new KlijentPlanId(k1.getId(), p1.getId()));
        kp1.setKlijent(k1);
        kp1.setPlan(p1);
        kp1.setDatumDodjele(LocalDate.of(2025,1,1));
        kp1.setStatus("Aktivan");
        klijentPlanRepo.save(kp1);

        KlijentPlan kp2 = new KlijentPlan();
        kp2.setId(new KlijentPlanId(k2.getId(), p2.getId()));
        kp2.setKlijent(k2);
        kp2.setPlan(p2);
        kp2.setDatumDodjele(LocalDate.of(2025,1,2));
        kp2.setStatus("Aktivan");
        klijentPlanRepo.save(kp2);

        List<KlijentPlan> forK1 = klijentPlanRepo.findByKlijentId(k1.getId());
        assertThat(forK1).hasSize(1)
            .first()
            .extracting(KlijentPlan::getPlan)
            .extracting(Plan::getNazivPlana)
            .isEqualTo("Plan A");
    }

    @Test
    @DisplayName("CRUD cycle works")
    void crudCycle() {
        // create Korisnik + Klijent
        Korisnik user = korisnikRepo.save(new KorisnikBuilder().email("user@example.com").build());
        Klijent klijent = new Klijent();
        klijent.setKorisnik(user);
        klijentRepo.save(klijent);

        // create Plan
        Plan plan = planRepo.save(new PlanBuilder().naziv("Cycling").build());

        // create KlijentPlan
        KlijentPlan kp = new KlijentPlan();
        kp.setId(new KlijentPlanId(klijent.getId(), plan.getId()));
        kp.setKlijent(klijent);
        kp.setPlan(plan);
        kp.setDatumDodjele(LocalDate.of(2025,5,1));
        kp.setStatus("Aktivan");
        klijentPlanRepo.save(kp);

        // read
        KlijentPlan fetched = klijentPlanRepo.findById(kp.getId()).orElseThrow();
        assertThat(fetched.getStatus()).isEqualTo("Aktivan");

        // update
        fetched.setStatus("Neaktivan");
        klijentPlanRepo.save(fetched);
        KlijentPlan updated = klijentPlanRepo.findById(kp.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("Neaktivan");

        // delete
        klijentPlanRepo.delete(updated);
        assertThat(klijentPlanRepo.findById(kp.getId())).isEmpty();
    }

    static class KorisnikBuilder {
        private final Korisnik u = new Korisnik();
        KorisnikBuilder email(String e) { u.setEmail(e); return this; }
        Korisnik build() { return u; }
    }
    static class PlanBuilder {
    private final Plan p = new Plan();

    PlanBuilder naziv(String n) {
        p.setNazivPlana(n);
        return this;
    }

    PlanBuilder opis(String o) {
        p.setOpis(o);
        return this;
    }

    Plan build() {
        p.setDatumKreiranja(LocalDate.now());
        return p;
    }
}
}
