package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "PLAN")
@Getter
@Setter
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Plan_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Trener_ID")
    private Trener trener;

    @Column(name = "NazivPlana")
    private String nazivPlana;

    @Column(name = "Opis")
    private String opis;

    @Column(name = "DatumKreiranja")
    private LocalDate datumKreiranja;
}
