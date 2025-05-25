package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistrationRequest;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.KorisnikRepository;
import com.example.demo.repository.TrenerRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AuthService authService;
    @MockBean private JwtService jwtService;
    @MockBean private KorisnikRepository korisnikRepository;
    @MockBean private KlijentRepository klijentRepository;
    @MockBean private TrenerRepository trenerRepository;

    @Test
    @DisplayName("POST /api/auth/register – successful registration returns 200 + KorisnikDto")
    void register_success() throws Exception {
        when(korisnikRepository.findByEmail("a@b.com"))
            .thenReturn(Optional.empty());

        Korisnik saved = new Korisnik();
        saved.setId(42L);
        saved.setEmail("a@b.com");
        when(authService.register(any(RegistrationRequest.class)))
            .thenReturn(saved);

        when(klijentRepository.findById(42L)).thenReturn(Optional.empty());
        when(trenerRepository.findById(42L)).thenReturn(Optional.empty());

        String payload = """
            {
              "email":"a@b.com",
              "lozinka":"Abcd1234!",
              "firstName":"Marko",
              "lastName":"Marković"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("a@b.com"))
            .andExpect(jsonPath("$.role").isEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/register – bad request when email exists")
    void register_emailTaken() throws Exception {
        when(korisnikRepository.findByEmail("x@y.com"))
            .thenReturn(Optional.of(new Korisnik()));

        String payload = """
            {
              "email":"x@y.com",
              "lozinka":"Abcd1234!",
              "firstName":"A",
              "lastName":"B"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Email je već registriran."));
    }

    @Test
    @DisplayName("POST /api/auth/login – valid credentials returns token + user")
    void login_success() throws Exception {
        // Mock login success
        Korisnik user = new Korisnik();
        user.setId(7L);
        user.setEmail("u@d.com");
        when(authService.login(any(LoginRequest.class)))
            .thenReturn(Optional.of(user));

        // Mock JWT generation
        when(jwtService.generateToken("u@d.com"))
            .thenReturn("jwt-token");

        // Mock role lookup: no Klijent, but Trener exists
        when(klijentRepository.findById(7L))
            .thenReturn(Optional.empty());
        when(trenerRepository.findById(7L))
            .thenReturn(Optional.of(new Trener()));  // <<<<< OVDJE: Optional<Trener>

        String payload = """
            {
              "email":"u@d.com",
              "password":"whatever"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"))
            .andExpect(jsonPath("$.user.email").value("u@d.com"))
            .andExpect(jsonPath("$.user.role").value("TRENER"));
    }

    @Test
    @DisplayName("POST /api/auth/login – invalid credentials returns 401")
    void login_failure() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenReturn(Optional.empty());

        String payload = """
            {
              "email":"bad@u.com",
              "password":"nopass"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Invalid credentials"));
    }
}
