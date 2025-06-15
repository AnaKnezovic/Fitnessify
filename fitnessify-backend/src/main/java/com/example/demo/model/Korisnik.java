package com.example.demo.model;
import java.time.LocalDate;

import com.example.demo.model.enums.Uloga;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "KORISNIK")
@Getter
@Setter
@NoArgsConstructor
public class Korisnik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Korisnik_ID")
    private Long id;

    @Column(name = "Ime")
    private String ime;

    @Column(name = "Prezime")
    private String prezime;

    @Column(name = "Email", unique = true)
    private String email;

    @Column(name = "Lozinka")
    private String lozinka;

    @Column(name = "Spol")
    private String spol;

    @Column(name = "Dob")
    private Integer dob;

    @Column(name = "DatumRegistracije")
    private LocalDate datumRegistracije;

    @Enumerated(EnumType.STRING)
    @Column(name = "Uloga")
    private Uloga uloga;

}
