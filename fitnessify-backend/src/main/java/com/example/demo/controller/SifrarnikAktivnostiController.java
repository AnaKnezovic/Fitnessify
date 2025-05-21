package com.example.demo.controller;

import com.example.demo.model.Aktivnost;
import com.example.demo.repository.AktivnostRepository;
import com.example.demo.repository.KorisnikAktivnostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sifrarnik-aktivnosti")
public class SifrarnikAktivnostiController {

    private final AktivnostRepository aktivnostRepo;
    private final KorisnikAktivnostRepository korisnikAktRepo;

    public SifrarnikAktivnostiController(AktivnostRepository aktivnostRepo, KorisnikAktivnostRepository korisnikAktRepo) {
        this.aktivnostRepo = aktivnostRepo;
        this.korisnikAktRepo = korisnikAktRepo;
    }

    // GET all
    @GetMapping
    public List<Aktivnost> getAll() {
        return aktivnostRepo.findAll();
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<Aktivnost> getById(@PathVariable Long id) {
        return aktivnostRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public Aktivnost create(@RequestBody Aktivnost aktivnost) {
        if (aktivnostRepo.existsByVrstaAktivnosti(aktivnost.getVrstaAktivnosti())) {
            throw new RuntimeException("Aktivnost s tom vrstom već postoji!");
        }
        return aktivnostRepo.save(aktivnost);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Aktivnost> update(@PathVariable Long id, @RequestBody Aktivnost details) {
        Aktivnost aktivnost = aktivnostRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktivnost ne postoji!"));

        // Provjeri je li aktivnost povezana s korisničkim aktivnostima
        if (korisnikAktRepo.existsByAktivnost(aktivnost)) {
            throw new RuntimeException("Nije moguće urediti aktivnost koja se koristi kod korisnika!");
        }

        aktivnost.setVrstaAktivnosti(details.getVrstaAktivnosti());
        aktivnost.setKalorijePoSatu(details.getKalorijePoSatu());
        return ResponseEntity.ok(aktivnostRepo.save(aktivnost));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Aktivnost aktivnost = aktivnostRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktivnost ne postoji!"));

        if (korisnikAktRepo.existsByAktivnost(aktivnost)) {
            throw new RuntimeException("Nije moguće obrisati aktivnost koja se koristi kod korisnika!");
        }

        aktivnostRepo.delete(aktivnost);
        return ResponseEntity.noContent().build();
    }
}
