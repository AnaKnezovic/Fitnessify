package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class NoviDnevnikDTO {
    private Long korisnikId;
    private LocalDate datumObroka;
    private String vrstaObroka;
    private List<UnosNamirniceDTO> namirnice;
}
