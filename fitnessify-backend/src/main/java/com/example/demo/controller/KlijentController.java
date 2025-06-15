package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Klijent;
import com.example.demo.repository.KlijentRepository;

@RestController
@RequestMapping("/api/klijenti")
public class KlijentController {

    @Autowired
    private KlijentRepository klijentRepo;

    @GetMapping("/{id}")
    public Klijent getKlijent(@PathVariable Long id) {
        return klijentRepo.findById(id).orElseThrow();
    }
}
