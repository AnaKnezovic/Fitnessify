package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.KorisnikDto;
import com.example.demo.model.Klijent;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.KorisnikRepository;
import com.example.demo.repository.TrenerRepository;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private KorisnikRepository korisnikRepository;
    @Autowired
    private KlijentRepository klijentRepository;
    @Autowired
    private TrenerRepository trenerRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste prijavljeni!");
        }
        String email = authentication.getName();
        Korisnik korisnik = korisnikRepository.findByEmail(email).orElse(null);
        if (korisnik == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Korisnik ne postoji!");
        }

        Klijent klijent = klijentRepository.findById(korisnik.getId()).orElse(null);
        Trener trener = trenerRepository.findById(korisnik.getId()).orElse(null);
        String role = klijent != null ? "KLIJENT" : trener != null ? "TRENER" : null;

        return ResponseEntity.ok(new KorisnikDto(korisnik, klijent, trener, role));
    }
    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody Map<String, Object> update,
                                    org.springframework.security.core.Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Niste prijavljeni!");
            }
            String email = authentication.getName();
            Korisnik korisnik = korisnikRepository.findByEmail(email).orElse(null);
            if (korisnik == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Korisnik ne postoji!");
            }
            // Update basic fields
            if (update.containsKey("ime")) korisnik.setIme((String) update.get("ime"));
            if (update.containsKey("prezime")) korisnik.setPrezime((String) update.get("prezime"));
            if (update.containsKey("dob")) {
                Object dobObj = update.get("dob");
                if (dobObj != null && !dobObj.toString().isBlank())
                    korisnik.setDob(Integer.valueOf(dobObj.toString()));
            }
            if (update.containsKey("spol")) korisnik.setSpol((String) update.get("spol"));
            korisnikRepository.save(korisnik);

            // Update klijent/trener podataka ako postoje
            klijentRepository.findById(korisnik.getId()).ifPresent(klijent -> {
                if (update.containsKey("visina")) {
                    Object visinaObj = update.get("visina");
                    klijent.setVisina(visinaObj != null && !visinaObj.toString().isBlank() ? Double.valueOf(visinaObj.toString()) : null);
                }
                if (update.containsKey("tezina")) {
                    Object tezinaObj = update.get("tezina");
                    klijent.setTezina(tezinaObj != null && !tezinaObj.toString().isBlank() ? Double.valueOf(tezinaObj.toString()) : null);
                }
                if (update.containsKey("cilj")) klijent.setCilj((String) update.get("cilj"));
                klijentRepository.save(klijent);
            });

            trenerRepository.findById(korisnik.getId()).ifPresent(trener -> {
                if (update.containsKey("strucnost")) trener.setStrucnost((String) update.get("strucnost"));
                if (update.containsKey("godineIskustva")) {
                    Object godineObj = update.get("godineIskustva");
                    trener.setGodineIskustva(godineObj != null && !godineObj.toString().isBlank() ? Integer.valueOf(godineObj.toString()) : null);
                }
                trenerRepository.save(trener);
            });

            return ResponseEntity.ok("Podaci su ažurirani.");
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Došlo je do greške prilikom ažuriranja: " + e.getMessage());
        }
    }

}
