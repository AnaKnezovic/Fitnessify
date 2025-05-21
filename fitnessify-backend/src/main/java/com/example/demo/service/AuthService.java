package com.example.demo.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistrationRequest;
import com.example.demo.model.Klijent;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.KorisnikRepository;
import com.example.demo.repository.TrenerRepository;

@Service
public class AuthService {

    @Autowired
    private KorisnikRepository korisnikRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private TrenerRepository trenerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Korisnik register(RegistrationRequest request) {
        if (korisnikRepository.findByEmail(request.email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setIme(request.ime);
        korisnik.setPrezime(request.prezime);
        korisnik.setEmail(request.email);
        korisnik.setLozinka(passwordEncoder.encode(request.lozinka));
        korisnik.setSpol(request.spol);
        korisnik.setDob(request.dob);
        korisnik.setDatumRegistracije(LocalDate.now());
        korisnik = korisnikRepository.save(korisnik);

        if ("KLIJENT".equalsIgnoreCase(request.role)) {
            Klijent klijent = new Klijent();
            klijent.setKorisnik(korisnik);
            klijent.setVisina(request.visina);
            klijent.setTezina(request.tezina);
            klijent.setCilj(request.cilj);
            klijentRepository.save(klijent);
        } else if ("TRENER".equalsIgnoreCase(request.role)) {
            Trener trener = new Trener();
            trener.setKorisnik(korisnik);
            trener.setStrucnost(request.strucnost);
            trener.setGodineIskustva(request.godineIskustva);
            trenerRepository.save(trener);
        }

        return korisnik;
    }

    public Optional<Korisnik> login(LoginRequest request) {
        Optional<Korisnik> korisnik = korisnikRepository.findByEmail(request.email);
        if (korisnik.isPresent() && passwordEncoder.matches(request.lozinka, korisnik.get().getLozinka())) {
            return korisnik;
        }
        return Optional.empty();
    }

}
