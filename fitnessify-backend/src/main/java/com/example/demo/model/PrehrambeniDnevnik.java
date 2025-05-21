package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PREHRAMBENI_DNEVNIK")
@Getter
@Setter
@NoArgsConstructor
public class PrehrambeniDnevnik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Dnevnik_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Korisnik_ID")
    private Korisnik korisnik;

    @Column(name = "DatumObroka")
    private LocalDate datumObroka;

    @Column(name = "VrstaObroka")
    private String vrstaObroka;
    
}
