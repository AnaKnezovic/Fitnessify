package com.example.demo.controller;

import com.example.demo.dto.StatistikaDTO;
import com.example.demo.service.StatistikaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistika")
public class StatistikaController {
    @Autowired private StatistikaService statistikaService;

    @GetMapping("/mjesecna")
    public List<StatistikaDTO> mjesecnaStatistika(@RequestParam Long korisnikId) {
        return statistikaService.dohvatiMjesecnuStatistiku(korisnikId);
    }

    // Za trenera – ista metoda, ali s klijentId (ako želiš biti eksplicitna)
    @GetMapping("/mjesecna-klijent")
    public List<StatistikaDTO> mjesecnaStatistikaKlijent(@RequestParam Long klijentId) {
        return statistikaService.dohvatiMjesecnuStatistiku(klijentId);
    }
}
