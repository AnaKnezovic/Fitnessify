package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Korisnik;
import com.example.demo.model.PrehrambeniDnevnik;

public interface PrehrambeniDnevnikRepository extends JpaRepository<PrehrambeniDnevnik, Long> {
    List<PrehrambeniDnevnik> findByKorisnikAndDatumObroka(Korisnik korisnik, LocalDate datumObroka);
    PrehrambeniDnevnik findByKorisnikAndDatumObrokaAndVrstaObroka(Korisnik korisnik, LocalDate datumObroka, String vrstaObroka);
}
