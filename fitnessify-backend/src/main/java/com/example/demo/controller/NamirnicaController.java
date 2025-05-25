package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Namirnica;
import com.example.demo.repository.NamirnicaRepository;
import com.example.demo.repository.UnosNamirniceRepository;

@RestController
@RequestMapping("/api/namirnice")
public class NamirnicaController {
    private final NamirnicaRepository namirnicaRepository;
    private final UnosNamirniceRepository unosNamirniceRepository;

    public NamirnicaController(NamirnicaRepository namirnicaRepository, UnosNamirniceRepository unosNamirniceRepository) {
        this.namirnicaRepository = namirnicaRepository;
        this.unosNamirniceRepository = unosNamirniceRepository;
    }

    // GET all
    @GetMapping
    public List<Namirnica> getAllNamirnice() {
        return namirnicaRepository.findAll();
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<Namirnica> getNamirnicaById(@PathVariable Long id) {
        return namirnicaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public Namirnica createNamirnica(@RequestBody Namirnica namirnica) {
        if (namirnicaRepository.existsByNaziv(namirnica.getNaziv())) {
            throw new RuntimeException("Namirnica s tim nazivom već postoji!");
        }
        return namirnicaRepository.save(namirnica);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Namirnica> updateNamirnica(@PathVariable Long id, @RequestBody Namirnica namirnicaDetails) {
        Namirnica namirnica = namirnicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Namirnica ne postoji!"));

        // Provjeri je li namirnica povezana s unosima
        if (unosNamirniceRepository.existsByNamirnica(namirnica)) {
            throw new RuntimeException("Nije moguće urediti namirnicu koja se koristi u dnevniku!");
        }

        if (!namirnica.getNaziv().equals(namirnicaDetails.getNaziv())
                && namirnicaRepository.existsByNaziv(namirnicaDetails.getNaziv())) {
            throw new RuntimeException("Namirnica s tim nazivom već postoji!");
        }

        namirnica.setNaziv(namirnicaDetails.getNaziv());
        namirnica.setKalorije(namirnicaDetails.getKalorije());
        namirnica.setProteini(namirnicaDetails.getProteini());
        namirnica.setUgljikohidrati(namirnicaDetails.getUgljikohidrati());
        namirnica.setMasti(namirnicaDetails.getMasti());
        return ResponseEntity.ok(namirnicaRepository.save(namirnica));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNamirnica(@PathVariable Long id) {
        Namirnica namirnica = namirnicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Namirnica ne postoji!"));

        // Provjeri je li namirnica povezana s unosima
        if (unosNamirniceRepository.existsByNamirnica(namirnica)) {
            throw new RuntimeException("Nije moguće obrisati namirnicu koja se koristi u dnevniku!");
        }

        namirnicaRepository.delete(namirnica);
        return ResponseEntity.noContent().build();
    }
}
