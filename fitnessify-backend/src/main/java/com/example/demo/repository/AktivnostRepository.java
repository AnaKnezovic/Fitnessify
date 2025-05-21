package com.example.demo.repository;

import com.example.demo.model.Aktivnost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AktivnostRepository extends JpaRepository<Aktivnost, Long> {
    boolean existsByVrstaAktivnosti(String vrstaAktivnosti);
}
