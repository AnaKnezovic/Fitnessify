package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AKTIVNOST")
@Getter
@Setter
@NoArgsConstructor
public class Aktivnost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Aktivnost_ID")
    private Long id;

    @Column(name = "VrstaAktivnosti")
    private String vrstaAktivnosti;

    @Column(name = "KalorijePoSatu")
    private Double kalorijePoSatu;
}
