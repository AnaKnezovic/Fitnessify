package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planovi")
public class PlanController {
    @Autowired private PlanRepository planRepo;
    @Autowired private KlijentPlanRepository klijentPlanRepo;
    @Autowired private KlijentRepository klijentRepo;
    @Autowired private TrenerRepository trenerRepo; // Dodaj ovo!

    // Dohvati sve planove trenera
    @GetMapping("/{trenerId}")
    public List<Plan> treneroviPlanovi(@PathVariable Long trenerId) {
        return planRepo.findByTrenerId(trenerId);
    }

    // Dodijeli plan klijentu
    @PostMapping("/dodijeli")
    public ResponseEntity<?> dodijeliPlan(@RequestBody Map<String, Object> body) {
        Long klijentId = Long.valueOf(body.get("klijentId").toString());
        Long planId = Long.valueOf(body.get("planId").toString());
        Klijent klijent = klijentRepo.findById(klijentId).orElseThrow();
        Plan plan = planRepo.findById(planId).orElseThrow();

        KlijentPlanId id = new KlijentPlanId(klijentId, planId);
        KlijentPlan kp = new KlijentPlan();
        kp.setId(id);
        kp.setKlijent(klijent);
        kp.setPlan(plan);
        kp.setDatumDodjele(LocalDate.now());
        kp.setStatus("Aktivan");
        klijentPlanRepo.save(kp);
        return ResponseEntity.ok(kp);
    }

    // Dohvati sve planove klijenta
    @GetMapping("/moji")
    public List<KlijentPlan> mojiPlanovi(@RequestParam Long klijentId) {
        return klijentPlanRepo.findByKlijentId(klijentId);
    }

    // Kreiraj novi plan
    @PostMapping
    public ResponseEntity<?> kreirajPlan(@RequestBody Map<String, Object> body) {
        String nazivPlana = body.get("nazivPlana").toString();
        String opis = body.get("opis").toString();
        Long trenerId = Long.valueOf(body.get("trenerId").toString());
        Trener trener = trenerRepo.findById(trenerId).orElseThrow();

        Plan plan = new Plan();
        plan.setNazivPlana(nazivPlana);
        plan.setOpis(opis);
        plan.setTrener(trener);
        plan.setDatumKreiranja(LocalDate.now());
        planRepo.save(plan);
        return ResponseEntity.ok(plan);
    }
}
