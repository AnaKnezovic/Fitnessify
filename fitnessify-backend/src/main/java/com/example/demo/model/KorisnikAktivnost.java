package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "KORISNIK_AKTIVNOST")
@Getter @Setter @NoArgsConstructor
public class KorisnikAktivnost {
    @EmbeddedId
    private KorisnikAktivnostId id;

    @ManyToOne
    @MapsId("korisnikId")
    @JoinColumn(name = "Korisnik_ID")
    private Korisnik korisnik;

    @ManyToOne
    @MapsId("aktivnostId")
    @JoinColumn(name = "Aktivnost_ID")
    private Aktivnost aktivnost;

    @Column(name = "Trajanje")
    private Integer trajanje; // u minutama

    @Column(name = "PotroseneKalorije")
    private Double potroseneKalorije;
}
