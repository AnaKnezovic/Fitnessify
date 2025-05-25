package com.example.demo.service;

import com.example.demo.dto.StatistikaDTO;
import com.example.demo.repository.KorisnikAktivnostRepository;
import com.example.demo.repository.UnosNamirniceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatistikaServiceTest {

    @Mock
    private UnosNamirniceRepository unosRepo;

    @Mock
    private KorisnikAktivnostRepository aktivnostRepo;

    @InjectMocks
    private StatistikaService service;

    @Test
    void dohvatiMjesecnuStatistiku_returns30Days() {
        Long korisnikId = 123L;

        // Mockiramo da unosRepo.ukupnoKalorijaZaDatum vraća dayOfMonth * 1.0
        when(unosRepo.ukupnoKalorijaZaDatum(eq(korisnikId), any(LocalDate.class)))
            .thenAnswer(inv -> {
                LocalDate d = inv.getArgument(1);
                return d.getDayOfMonth() * 1.0;
            });
        // Mockiramo da aktivnostRepo.ukupnoPotroseneKalorijeZaDatum vraća dayOfMonth * 2.0
        when(aktivnostRepo.ukupnoPotroseneKalorijeZaDatum(eq(korisnikId), any(LocalDate.class)))
            .thenAnswer(inv -> {
                LocalDate d = inv.getArgument(1);
                return d.getDayOfMonth() * 2.0;
            });

        // Pozivamo servis
        List<StatistikaDTO> stats = service.dohvatiMjesecnuStatistiku(korisnikId);

        // Trebalo bi biti 30 dana
        assertEquals(30, stats.size());

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(29);

        for (int i = 0; i < 30; i++) {
            StatistikaDTO dto = stats.get(i);
            LocalDate expectedDate = start.plusDays(i);

            assertEquals(expectedDate, dto.getDatum(), "Datum ne odgovara");
            double expectedUnos = expectedDate.getDayOfMonth() * 1.0;
            double expectedPotrosnja = expectedDate.getDayOfMonth() * 2.0;
            assertEquals(expectedUnos, dto.getUnosKalorija(), 1e-6, "Unos kalorija ne odgovara");
            assertEquals(expectedPotrosnja, dto.getPotrosnjaKalorija(), 1e-6, "Potrosnja kalorija ne odgovara");
            assertEquals(expectedUnos - expectedPotrosnja, dto.getBilanca(), 1e-6, "Bilanca ne odgovara");
        }

        // Provjera da je svaki repo pozvan 30 puta
        verify(unosRepo, times(30)).ukupnoKalorijaZaDatum(eq(korisnikId), any(LocalDate.class));
        verify(aktivnostRepo, times(30)).ukupnoPotroseneKalorijeZaDatum(eq(korisnikId), any(LocalDate.class));
    }
}

