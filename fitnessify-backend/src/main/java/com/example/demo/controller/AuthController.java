package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.KorisnikDto;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistrationRequest;
import com.example.demo.model.Korisnik;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private com.example.demo.repository.KlijentRepository klijentRepository;

    @Autowired
    private com.example.demo.repository.KorisnikRepository korisnikRepository;

    @Autowired
    private com.example.demo.repository.TrenerRepository trenerRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            if (korisnikRepository.findByEmail(request.email).isPresent()) {
                return ResponseEntity.badRequest().body("Email je već registriran.");
            }

            String lozinka = request.lozinka;
            String passwordError = validatePassword(lozinka);
            if (passwordError != null) {
                return ResponseEntity.badRequest().body(passwordError);
            }

            Korisnik korisnik = authService.register(request);

            var klijent = klijentRepository.findById(korisnik.getId()).orElse(null);
            var trener = trenerRepository.findById(korisnik.getId()).orElse(null);
            String role = klijent != null ? "KLIJENT" : trener != null ? "TRENER" : null;

            return ResponseEntity.ok(new KorisnikDto(korisnik, klijent, trener, role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Dodaj ovu metodu u AuthController ili AuthService
    private String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Lozinka mora imati minimalno 8 znakova.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Lozinka mora sadržavati barem jedno veliko slovo.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Lozinka mora sadržavati barem jedno malo slovo.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Lozinka mora sadržavati barem jedan broj.";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "Lozinka mora sadržavati barem jedan poseban znak.";
        }
        return null; // Sve ok!
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var korisnikOpt = authService.login(request);
        if (korisnikOpt.isPresent()) {
            Korisnik korisnik = korisnikOpt.get();
            String token = jwtService.generateToken(korisnik.getEmail());

            var klijent = klijentRepository.findById(korisnik.getId()).orElse(null);
            var trener = trenerRepository.findById(korisnik.getId()).orElse(null);
            String role = klijent != null ? "KLIJENT" : trener != null ? "TRENER" : null;

            return ResponseEntity.ok(Map.of(
                "token", token,
                "user", new KorisnikDto(korisnik, klijent, trener, role)
            ));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

}
