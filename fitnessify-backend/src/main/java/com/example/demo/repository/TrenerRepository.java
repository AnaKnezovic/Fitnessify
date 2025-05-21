package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Trener;

public interface TrenerRepository extends JpaRepository<Trener, Long> {
}
