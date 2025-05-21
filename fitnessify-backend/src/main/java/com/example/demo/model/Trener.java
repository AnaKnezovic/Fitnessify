package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TRENER")
@Getter
@Setter
@NoArgsConstructor
public class Trener {
    @Id
    @Column(name = "Trener_ID")
    private Long id; 

    @OneToOne
    @MapsId
    @JoinColumn(name = "Trener_ID")
    private Korisnik korisnik;

    @Column(name = "Strucnost")
    private String strucnost;

    @Column(name = "GodineIskustva")
    private Integer godineIskustva;


}
