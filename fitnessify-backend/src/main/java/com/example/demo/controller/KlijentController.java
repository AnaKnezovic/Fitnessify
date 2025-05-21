package com.example.demo.controller;

import com.example.demo.model.Klijent;
import com.example.demo.repository.KlijentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/klijenti")
public class KlijentController {

    @Autowired
    private KlijentRepository klijentRepo;

    @GetMapping("/{id}")
    public Klijent getKlijent(@PathVariable Long id) {
        return klijentRepo.findById(id).orElseThrow();
    }

    // Dodaj još metode po potrebi (npr. lista svih klijenata)
}
