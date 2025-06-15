package com.example.demo.repository;

import java.util.List;
import com.example.demo.model.Obavijest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObavijestRepository extends JpaRepository<Obavijest, Long> {
    List<Obavijest> findByKorisnikId(Long korisnikId);
}
