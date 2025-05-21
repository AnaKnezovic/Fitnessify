package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Namirnica;
import com.example.demo.model.PrehrambeniDnevnik;
import com.example.demo.model.UnosNamirnice;
import com.example.demo.model.UnosNamirnice.UnosNamirniceId;

public interface UnosNamirniceRepository extends JpaRepository<UnosNamirnice, UnosNamirniceId> {
    List<UnosNamirnice> findByDnevnik(PrehrambeniDnevnik dnevnik);
    UnosNamirnice findByDnevnikAndNamirnica(PrehrambeniDnevnik dnevnik, Namirnica namirnica);
    boolean existsByNamirnica(Namirnica namirnica);

    @Query("SELECT COALESCE(SUM(u.kolicina * u.namirnica.kalorije / 100), 0) " +
           "FROM UnosNamirnice u " +
           "WHERE u.dnevnik.korisnik.id = :korisnikId AND u.dnevnik.datumObroka = :datum")
    Double ukupnoKalorijaZaDatum(@Param("korisnikId") Long korisnikId, @Param("datum") LocalDate datum);
}
