package com.example.demo.service;

import com.example.demo.dto.StatistikaDTO;
import com.example.demo.repository.UnosNamirniceRepository;
import com.example.demo.repository.KorisnikAktivnostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StatistikaService {
    @Autowired private UnosNamirniceRepository unosNamirniceRepo;
    @Autowired private KorisnikAktivnostRepository aktivnostRepo;

    public List<StatistikaDTO> dohvatiMjesecnuStatistiku(Long korisnikId) {
        LocalDate danas = LocalDate.now();
        LocalDate prije30Dana = danas.minusDays(29);
        List<StatistikaDTO> statistika = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            LocalDate datum = prije30Dana.plusDays(i);
            double unos = Optional.ofNullable(unosNamirniceRepo.ukupnoKalorijaZaDatum(korisnikId, datum)).orElse(0.0);
            double potrosnja = Optional.ofNullable(aktivnostRepo.ukupnoPotroseneKalorijeZaDatum(korisnikId, datum)).orElse(0.0);
            statistika.add(new StatistikaDTO(datum, unos, potrosnja));
        }
        return statistika;
    }
}
