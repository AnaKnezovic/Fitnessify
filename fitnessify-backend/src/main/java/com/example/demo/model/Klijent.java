package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "KLIJENT")
@Getter
@Setter
@NoArgsConstructor
public class Klijent {
    @Id
    @Column(name = "Klijent_ID")
    private Long id; // same as Korisnik_ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "Klijent_ID")
    private Korisnik korisnik;

    @Column(name = "Visina")
    private Double visina;

    @Column(name = "Tezina")
    private Double tezina;

    @Column(name = "Cilj")
    private String cilj;

    // Getters and Setters
}
