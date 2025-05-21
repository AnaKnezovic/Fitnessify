package com.example.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "UNOS_NAMIRNICE")
@Getter
@Setter
@NoArgsConstructor
@IdClass(UnosNamirnice.UnosNamirniceId.class)
public class UnosNamirnice {

    @Id
    @ManyToOne
    @JoinColumn(name = "Dnevnik_ID")
    private PrehrambeniDnevnik dnevnik;

    @Id
    @ManyToOne
    @JoinColumn(name = "Namirnica_ID")
    private Namirnica namirnica;

    @Column(name = "Kolicina")
    private BigDecimal kolicina;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UnosNamirniceId implements Serializable {
        private Long dnevnik;
        private Long namirnica;
    }
}
