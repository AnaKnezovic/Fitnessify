package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Klijent;

public interface KlijentRepository extends JpaRepository<Klijent, Long> {
}
