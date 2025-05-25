package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegistrationRequest;
import com.example.demo.model.Klijent;
import com.example.demo.model.Korisnik;
import com.example.demo.model.Trener;
import com.example.demo.repository.KlijentRepository;
import com.example.demo.repository.KorisnikRepository;
import com.example.demo.repository.TrenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KorisnikRepository korisnikRepository;
    @Mock
    private KlijentRepository klijentRepository;
    @Mock
    private TrenerRepository trenerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegistrationRequest regReq;
    private LoginRequest loginReq;

    @BeforeEach
    void setup() {
        regReq = new RegistrationRequest();
        regReq.ime = "Test";
        regReq.prezime = "User";
        regReq.email = "test@example.com";
        regReq.lozinka = "rawpass";
        regReq.spol = "M";
        regReq.dob = 25;
        regReq.role = "KLIJENT";
        regReq.visina = 180.0;
        regReq.tezina = 75.0;
        regReq.cilj = "Fitness";
        // trainer fields left null

        loginReq = new LoginRequest();
        loginReq.email = "test@example.com";
        loginReq.lozinka = "Password1!";
    }

    @Test
    @DisplayName("register: new client – saves Korisnik and Klijent")
    void register_newClient() {
        when(korisnikRepository.findByEmail(regReq.email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(regReq.lozinka)).thenReturn("encoded");
        // pretend save returns entity with ID
        ArgumentCaptor<Korisnik> capK = ArgumentCaptor.forClass(Korisnik.class);
        when(korisnikRepository.save(capK.capture())).thenAnswer(i -> {
            Korisnik k = i.getArgument(0);
            k.setId(1L);
            return k;
        });
        ArgumentCaptor<Klijent> capKl = ArgumentCaptor.forClass(Klijent.class);
        when(klijentRepository.save(capKl.capture())).thenAnswer(i -> i.getArgument(0));

        Korisnik created = authService.register(regReq);

        // verify korisnik saved with encoded password and date
        assertEquals("Test", created.getIme());
        assertEquals("encoded", created.getLozinka());
        assertNotNull(created.getDatumRegistracije());
        verify(korisnikRepository).save(any());

        // verify klijent saved
        Klijent savedKl = capKl.getValue();
        assertEquals(1L, savedKl.getKorisnik().getId());
        assertEquals(180.0, savedKl.getVisina());
        assertEquals("Fitness", savedKl.getCilj());
    }

    @Test
    @DisplayName("register: duplicate email → RuntimeException")
    void register_duplicateEmail() {
        when(korisnikRepository.findByEmail(regReq.email))
            .thenReturn(Optional.of(new Korisnik()));
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> authService.register(regReq));
        assertEquals("Email already registered", ex.getMessage());
        verify(korisnikRepository, never()).save(any());
    }

    @Test
    @DisplayName("register: trainer role – saves Trener")
    void register_newTrainer() {
        regReq.role = "TRENER";
        regReq.strucnost = "PT";
        regReq.godineIskustva = 5;

        when(korisnikRepository.findByEmail(regReq.email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("enc");
        when(korisnikRepository.save(any())).thenAnswer(i -> {
            Korisnik k = i.getArgument(0);
            k.setId(2L);
            return k;
        });

        authService.register(regReq);

        // verify trenerRepository.save was called
        ArgumentCaptor<Trener> capT = ArgumentCaptor.forClass(Trener.class);
        verify(trenerRepository).save(capT.capture());
        Trener savedT = capT.getValue();
        assertEquals(2L, savedT.getKorisnik().getId());
        assertEquals("PT", savedT.getStrucnost());
    }

    @Test
    @DisplayName("login: correct creds → returns user")
    void login_success() {
        Korisnik k = new Korisnik();
        k.setLozinka("encoded");
        when(korisnikRepository.findByEmail(loginReq.email))
            .thenReturn(Optional.of(k));
        when(passwordEncoder.matches("Password1!", "encoded")).thenReturn(true);

        Optional<Korisnik> out = authService.login(loginReq);
        assertTrue(out.isPresent());
        assertSame(k, out.get());
    }

    @Test
    @DisplayName("login: wrong password → empty")
    void login_badPassword() {
        Korisnik k = new Korisnik();
        k.setLozinka("encoded");
        when(korisnikRepository.findByEmail(loginReq.email))
            .thenReturn(Optional.of(k));
        when(passwordEncoder.matches("Password1!", "encoded")).thenReturn(false);

        assertTrue(authService.login(loginReq).isEmpty());
    }

    @Test
    @DisplayName("login: email not found → empty")
    void login_noUser() {
        when(korisnikRepository.findByEmail(loginReq.email))
            .thenReturn(Optional.empty());
        assertTrue(authService.login(loginReq).isEmpty());
    }
}
