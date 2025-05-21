package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "KLIJENT_TRENER")
@Getter
@Setter
@NoArgsConstructor
public class KlijentTrener {
    @EmbeddedId
    private KlijentTrenerId id;

    @ManyToOne
    @MapsId("klijentId")
    @JoinColumn(name = "Klijent_ID")
    private Klijent klijent;

    @ManyToOne
    @MapsId("trenerId")
    @JoinColumn(name = "Trener_ID")
    private Trener trener;

    @Column(name = "DatumPovezivanja")
    private LocalDate datumPovezivanja;

    @Column(name = "Status")
    private String status;
}
