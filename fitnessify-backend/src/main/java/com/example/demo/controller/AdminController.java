package com.example.demo.controller;

import com.example.demo.dto.KlijentTrenerDTO;
import com.example.demo.dto.ObavijestDTO;
import com.example.demo.model.KlijentTrener;
import com.example.demo.repository.KlijentTrenerRepository;
import com.example.demo.repository.ObavijestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private KlijentTrenerRepository klijentTrenerRepository;
    @Autowired
    private ObavijestRepository obavijestRepository;

    @GetMapping("/veze")
    public List<KlijentTrenerDTO> sveVeze() {
        return klijentTrenerRepository.findAll()
            .stream()
            .map(z -> new KlijentTrenerDTO(
                    z.getKlijent().getId(),
                    z.getTrener().getId(),
                    z.getStatus(),
                    z.getDatumPovezivanja()))
            .collect(Collectors.toList());
    }

    @GetMapping("/obavijesti")
    public List<ObavijestDTO> sveObavijesti() {
        return obavijestRepository.findAll()
            .stream()
            .map(o -> new ObavijestDTO(o.getKorisnikId(), o.getTekst(), o.getVrijeme()))
            .collect(Collectors.toList());
    }
}
