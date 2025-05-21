package com.example.demo.repository;

import com.example.demo.model.Aktivnost;
import com.example.demo.model.KorisnikAktivnost;
import com.example.demo.model.KorisnikAktivnostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface KorisnikAktivnostRepository extends JpaRepository<KorisnikAktivnost, KorisnikAktivnostId> {
    List<KorisnikAktivnost> findByKorisnikIdAndIdDatumAktivnosti(Long korisnikId, LocalDate datum);
    List<KorisnikAktivnost> findByKorisnikId(Long korisnikId);
    boolean existsByAktivnost(Aktivnost aktivnost);
    @Query("SELECT COALESCE(SUM(a.potroseneKalorije), 0) " +
           "FROM KorisnikAktivnost a " +
           "WHERE a.korisnik.id = :korisnikId AND a.id.datumAktivnosti = :datum")
    Double ukupnoPotroseneKalorijeZaDatum(@Param("korisnikId") Long korisnikId, @Param("datum") LocalDate datum);
}
