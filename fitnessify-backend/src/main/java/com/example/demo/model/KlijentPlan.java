package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "KLIJENT_PLAN")
@Getter @Setter @NoArgsConstructor
public class KlijentPlan {
    @EmbeddedId
    private KlijentPlanId id;

    @ManyToOne
    @MapsId("klijentId")
    @JoinColumn(name = "Klijent_ID")
    private Klijent klijent;

    @ManyToOne
    @MapsId("planId")
    @JoinColumn(name = "Plan_ID")
    private Plan plan;

    @Column(name = "DatumDodjele")
    private LocalDate datumDodjele;

    @Column(name = "Status")
    private String status;
}
