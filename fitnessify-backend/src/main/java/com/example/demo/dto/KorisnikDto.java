package com.example.demo.dto;

import com.example.demo.model.Klijent;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Trener;

public class KorisnikDto {
    public Long id;
    public String ime;
    public String prezime;
    public String email;
    public String spol;
    public Integer dob;
    public String role;
    public Double visina;
    public Double tezina;
    public String cilj;
    public String strucnost;
    public Integer godineIskustva;
    public String datumRegistracije;

    public KorisnikDto(Korisnik korisnik, Klijent klijent, Trener trener, String role) {
        this.id = korisnik.getId();
        this.ime = korisnik.getIme();
        this.prezime = korisnik.getPrezime();
        this.email = korisnik.getEmail();
        this.spol = korisnik.getSpol();
        this.dob = korisnik.getDob();
        this.role = role;
        this.datumRegistracije = korisnik.getDatumRegistracije() != null ? korisnik.getDatumRegistracije().toString() : null;

        if (klijent != null) {
            this.visina = klijent.getVisina();
            this.tezina = klijent.getTezina();
            this.cilj = klijent.getCilj();
        }
        
        if (trener != null) {
            this.strucnost = trener.getStrucnost();
            this.godineIskustva = trener.getGodineIskustva();
        }
    }
}
