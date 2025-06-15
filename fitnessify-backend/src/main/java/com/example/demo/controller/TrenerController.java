package com.example.demo.controller;

import com.example.demo.dto.KlijentTrenerDTO;
import com.example.demo.dto.ObavijestDTO;
import com.example.demo.dto.OdlukaDTO;
import com.example.demo.model.KlijentTrener;
import com.example.demo.model.KlijentTrenerId;
import com.example.demo.repository.KlijentTrenerRepository;
import com.example.demo.repository.ObavijestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trener")
public class TrenerController {

    @Autowired
    private KlijentTrenerRepository klijentTrenerRepository;
    @Autowired
    private ObavijestRepository obavijestRepository;

    @GetMapping("/zahtjevi/{trenerId}")
    public List<KlijentTrenerDTO> zahtjeviZaTrenera(@PathVariable Long trenerId) {
        return klijentTrenerRepository.findByTrener_IdAndStatus(trenerId, "POSLAN")
            .stream()
            .map(z -> new KlijentTrenerDTO(
                    z.getKlijent().getId(),
                    z.getTrener().getId(),
                    z.getStatus(),
                    z.getDatumPovezivanja()))
            .collect(Collectors.toList());
    }

    @PostMapping("/zahtjevi/{klijentId}/{trenerId}/odluka")
    public ResponseEntity<?> odluka(@PathVariable Long klijentId, @PathVariable Long trenerId, @RequestBody OdlukaDTO odluka) {
        KlijentTrenerId id = new KlijentTrenerId(klijentId, trenerId);
        KlijentTrener zahtjev = klijentTrenerRepository.findById(id).orElseThrow();
        zahtjev.setStatus(odluka.getStatus());
        klijentTrenerRepository.save(zahtjev);
        // (Opcionalno: kompletiraj Camunda task ovdje)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obavijesti/{trenerId}")
    public List<ObavijestDTO> obavijestiZaTrenera(@PathVariable Long trenerId) {
        return obavijestRepository.findByKorisnikId(trenerId)
            .stream()
            .map(o -> new ObavijestDTO(o.getKorisnikId(), o.getTekst(), o.getVrijeme()))
            .collect(Collectors.toList());
    }
}
