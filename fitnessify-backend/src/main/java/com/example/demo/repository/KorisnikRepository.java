package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Korisnik;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long> {
    Optional<Korisnik> findByEmail(String email);  // Useful for login
}
