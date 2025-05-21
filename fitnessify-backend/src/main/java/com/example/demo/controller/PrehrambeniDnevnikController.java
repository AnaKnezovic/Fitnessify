package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.NoviDnevnikDTO;
import com.example.demo.dto.UnosNamirniceDTO;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Namirnica;
import com.example.demo.model.PrehrambeniDnevnik;
import com.example.demo.model.UnosNamirnice;
import com.example.demo.repository.KorisnikRepository;
import com.example.demo.repository.NamirnicaRepository;
import com.example.demo.repository.PrehrambeniDnevnikRepository;
import com.example.demo.repository.UnosNamirniceRepository;

@RestController
@RequestMapping("/api/dnevnik")
public class PrehrambeniDnevnikController {

    private final PrehrambeniDnevnikRepository dnevnikRepo;
    private final KorisnikRepository korisnikRepo;
    private final NamirnicaRepository namirnicaRepo;
    private final UnosNamirniceRepository unosRepo;

    public PrehrambeniDnevnikController(
            PrehrambeniDnevnikRepository dnevnikRepo,
            KorisnikRepository korisnikRepo,
            NamirnicaRepository namirnicaRepo,
            UnosNamirniceRepository unosRepo
    ) {
        this.dnevnikRepo = dnevnikRepo;
        this.korisnikRepo = korisnikRepo;
        this.namirnicaRepo = namirnicaRepo;
        this.unosRepo = unosRepo;
    }

    @PostMapping
    public PrehrambeniDnevnik addDnevnik(@RequestBody NoviDnevnikDTO dto) {
        Korisnik korisnik = korisnikRepo.findById(dto.getKorisnikId())
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji!"));
        PrehrambeniDnevnik dnevnik = dnevnikRepo.findByKorisnikAndDatumObrokaAndVrstaObroka(
                korisnik, dto.getDatumObroka(), dto.getVrstaObroka()
        );
        if (dnevnik == null) {
            dnevnik = new PrehrambeniDnevnik();
            dnevnik.setKorisnik(korisnik);
            dnevnik.setDatumObroka(dto.getDatumObroka());
            dnevnik.setVrstaObroka(dto.getVrstaObroka());
            dnevnik = dnevnikRepo.save(dnevnik);
        }
        for (UnosNamirniceDTO unosDTO : dto.getNamirnice()) {
            Namirnica namirnica = namirnicaRepo.findById(unosDTO.getNamirnicaId())
                    .orElseThrow(() -> new RuntimeException("Namirnica ne postoji!"));
            if (unosRepo.findByDnevnikAndNamirnica(dnevnik, namirnica) != null) {
                throw new RuntimeException("Ova namirnica je već unesena za ovaj obrok i datum!");
            }
            UnosNamirnice unos = new UnosNamirnice();
            unos.setDnevnik(dnevnik);
            unos.setNamirnica(namirnica);
            unos.setKolicina(unosDTO.getKolicina());
            unosRepo.save(unos);
        }
        return dnevnik;
    }


    // Dohvat svih dnevnika za korisnika i datum
    @GetMapping("/{korisnikId}/{datum}")
    public List<PrehrambeniDnevnik> getDnevnikZaDan(
            @PathVariable Long korisnikId,
            @PathVariable String datum
    ) {
        Korisnik korisnik = korisnikRepo.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji!"));
        return dnevnikRepo.findByKorisnikAndDatumObroka(korisnik, LocalDate.parse(datum));
    }

    // Dohvat svih unosa za jedan dnevnik
    @GetMapping("/unosi/{dnevnikId}")
    public List<UnosNamirnice> getUnosiZaDnevnik(@PathVariable Long dnevnikId) {
        PrehrambeniDnevnik dnevnik = dnevnikRepo.findById(dnevnikId)
                .orElseThrow(() -> new RuntimeException("Dnevnik ne postoji!"));
        return unosRepo.findByDnevnik(dnevnik);
    }

    // Dohvat svih unosa za korisnika i dan (svi obroci)
    @GetMapping("/svi-unosi/{korisnikId}/{datum}")
    public List<UnosNamirnice> getSviUnosiZaDan(
            @PathVariable Long korisnikId,
            @PathVariable String datum
    ) {
        Korisnik korisnik = korisnikRepo.findById(korisnikId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji!"));
        List<PrehrambeniDnevnik> dnevnici = dnevnikRepo.findByKorisnikAndDatumObroka(korisnik, LocalDate.parse(datum));
        List<UnosNamirnice> sviUnosi = new ArrayList<>();
        for (PrehrambeniDnevnik d : dnevnici) {
            sviUnosi.addAll(unosRepo.findByDnevnik(d));
        }
        return sviUnosi;
    }

    @DeleteMapping("/unos/{dnevnikId}/{namirnicaId}")
    public void deleteUnos(
        @PathVariable Long dnevnikId,
        @PathVariable Long namirnicaId
    ) {
        UnosNamirnice.UnosNamirniceId id = new UnosNamirnice.UnosNamirniceId();
        id.setDnevnik(dnevnikId);
        id.setNamirnica(namirnicaId);
        unosRepo.deleteById(id);
    }


    @PutMapping("/unos/{dnevnikId}/{namirnicaId}")
    public UnosNamirnice editUnos(
        @PathVariable Long dnevnikId,
        @PathVariable Long namirnicaId,
        @RequestBody UnosNamirnice unosIzFronta
    ) {
        UnosNamirnice.UnosNamirniceId id = new UnosNamirnice.UnosNamirniceId();
        id.setDnevnik(dnevnikId);
        id.setNamirnica(namirnicaId);
        UnosNamirnice unos = unosRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Unos ne postoji!"));
        unos.setKolicina(unosIzFronta.getKolicina());
        // Ako želiš omogućiti promjenu namirnice, to je složenije zbog ključa!
        return unosRepo.save(unos);
    }

}
