package com.example.demo.dto;

import java.time.LocalDate;

public class StatistikaDTO {
    private final LocalDate datum;
    private final double unosKalorija;
    private final double potrosnjaKalorija;
    private final double bilanca;

    public StatistikaDTO(LocalDate datum, double unosKalorija, double potrosnjaKalorija) {
        this.datum = datum;
        this.unosKalorija = unosKalorija;
        this.potrosnjaKalorija = potrosnjaKalorija;
        this.bilanca = unosKalorija - potrosnjaKalorija;
    }

    // Getteri i setteri
    public LocalDate getDatum() { return datum; }
    public double getUnosKalorija() { return unosKalorija; }
    public double getPotrosnjaKalorija() { return potrosnjaKalorija; }
    public double getBilanca() { return bilanca; }
}
