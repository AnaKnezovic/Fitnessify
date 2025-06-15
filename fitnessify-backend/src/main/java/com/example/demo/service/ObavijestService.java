package com.example.demo.service;

import com.example.demo.model.Obavijest;
import com.example.demo.repository.ObavijestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ObavijestService {

    @Autowired
    private ObavijestRepository obavijestRepository;

    public void posaljiObavijest(Long korisnikId, String tekst) {
        Obavijest o = new Obavijest();
        o.setKorisnikId(korisnikId);
        o.setTekst(tekst);
        o.setVrijeme(LocalDateTime.now());
        obavijestRepository.save(o);
    }
}
