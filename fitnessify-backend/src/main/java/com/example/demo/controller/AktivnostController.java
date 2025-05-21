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
@RequestMapping("/api/aktivnosti")
public class AktivnostController {

    @Autowired
    private AktivnostRepository aktivnostRepo;
    @Autowired
    private KorisnikAktivnostRepository korisnikAktRepo;
    @Autowired
    private KorisnikRepository korisnikRepo;

    // Dohvati sve vrste aktivnosti
    @GetMapping
    public List<Aktivnost> getAllAktivnosti() {
        return aktivnostRepo.findAll();
    }

    // Dodaj korisničku aktivnost
    @PostMapping("/korisnik")
    public ResponseEntity<?> addKorisnikAktivnost(@RequestBody Map<String, Object> body) {
        Long korisnikId = Long.valueOf(body.get("korisnikId").toString());
        Long aktivnostId = Long.valueOf(body.get("aktivnostId").toString());
        Integer trajanje = Integer.valueOf(body.get("trajanje").toString());
        LocalDate datum = LocalDate.parse(body.get("datum").toString());

        Korisnik korisnik = korisnikRepo.findById(korisnikId).orElseThrow();
        Aktivnost aktivnost = aktivnostRepo.findById(aktivnostId).orElseThrow();

        double potroseneKalorije = aktivnost.getKalorijePoSatu() * trajanje / 60.0;

        KorisnikAktivnostId id = new KorisnikAktivnostId(korisnikId, aktivnostId, datum);

        KorisnikAktivnost ka = new KorisnikAktivnost();
        ka.setId(id);
        ka.setKorisnik(korisnik);
        ka.setAktivnost(aktivnost);
        ka.setTrajanje(trajanje);
        ka.setPotroseneKalorije(potroseneKalorije);

        korisnikAktRepo.save(ka);

        return ResponseEntity.ok(ka);
    }

    // Dohvati aktivnosti korisnika za dan
    @GetMapping("/korisnik/{korisnikId}/{datum}")
    public List<KorisnikAktivnost> getKorisnikAktivnostiZaDan(@PathVariable Long korisnikId, @PathVariable String datum) {
        return korisnikAktRepo.findByKorisnikIdAndIdDatumAktivnosti(korisnikId, LocalDate.parse(datum));
    }

    // Brisanje aktivnosti
    @DeleteMapping("/korisnik")
    public ResponseEntity<?> deleteKorisnikAktivnost(@RequestParam Long korisnikId, @RequestParam Long aktivnostId, @RequestParam String datum) {
        KorisnikAktivnostId id = new KorisnikAktivnostId(korisnikId, aktivnostId, LocalDate.parse(datum));
        korisnikAktRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/korisnik")
    public ResponseEntity<?> updateKorisnikAktivnost(@RequestBody Map<String, Object> body) {
        Long korisnikId = Long.valueOf(body.get("korisnikId").toString());
        Long aktivnostId = Long.valueOf(body.get("aktivnostId").toString());
        Integer trajanje = Integer.valueOf(body.get("trajanje").toString());
        String datumStr = body.get("datum").toString();

        LocalDate datum = LocalDate.parse(datumStr);

        KorisnikAktivnostId id = new KorisnikAktivnostId(korisnikId, aktivnostId, datum);
        KorisnikAktivnost ka = korisnikAktRepo.findById(id).orElseThrow();

        // Ažuriraj trajanje i potrošene kalorije
        ka.setTrajanje(trajanje);
        double potroseneKalorije = ka.getAktivnost().getKalorijePoSatu() * trajanje / 60.0;
        ka.setPotroseneKalorije(potroseneKalorije);

        korisnikAktRepo.save(ka);

        return ResponseEntity.ok(ka);
    }

    @GetMapping("/klijent/{klijentId}")
    public List<KorisnikAktivnost> getSveAktivnostiKlijenta(@PathVariable Long klijentId) {
        return korisnikAktRepo.findByKorisnikId(klijentId);
    }

}
