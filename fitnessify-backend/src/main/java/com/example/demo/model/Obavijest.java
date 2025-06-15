package com.example.demo.model;

import java.time.LocalDateTime;

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
@Table(name = "OBAVIJEST")
@Getter
@Setter
@NoArgsConstructor
public class Obavijest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Korisnik_ID")
    private Long korisnikId;

    @Column(name = "Tekst")
    private String tekst;

    @Column(name = "Vrijeme")
    private LocalDateTime vrijeme;
}
