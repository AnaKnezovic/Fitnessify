package com.example.demo.repository;

import com.example.demo.model.KlijentPlan;
import com.example.demo.model.KlijentPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KlijentPlanRepository extends JpaRepository<KlijentPlan, KlijentPlanId> {
    List<KlijentPlan> findByKlijentId(Long klijentId);
}
