package com.example.demo.model;

import java.math.BigDecimal;

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
@Table(name = "NAMIRNICA")
@Getter
@Setter
@NoArgsConstructor
public class Namirnica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Namirnica_ID")
    private Long id;

    @Column(name = "Naziv")
    private String naziv;

    @Column(name = "Kalorije")
    private Integer kalorije;

    @Column(name = "Proteini")
    private BigDecimal proteini;

    @Column(name = "Ugljikohidrati")
    private BigDecimal ugljikohidrati;

    @Column(name = "Masti")
    private BigDecimal masti;
}
