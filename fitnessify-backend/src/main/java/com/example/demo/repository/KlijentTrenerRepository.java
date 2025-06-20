package com.example.demo.repository;

import com.example.demo.model.KlijentTrener;
import com.example.demo.model.KlijentTrenerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KlijentTrenerRepository extends JpaRepository<KlijentTrener, KlijentTrenerId> {
    List<KlijentTrener> findByKlijentId(Long klijentId);
    List<KlijentTrener> findByTrenerId(Long trenerId);
    List<KlijentTrener> findByTrener_IdAndStatus(Long trenerId, String status);
    List<KlijentTrener> findByKlijent_Id(Long klijentId);
    List<KlijentTrener> findAll();
}
