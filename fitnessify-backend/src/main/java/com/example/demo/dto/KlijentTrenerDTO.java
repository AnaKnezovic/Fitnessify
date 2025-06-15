package com.example.demo.dto;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class KlijentTrenerDTO {
    private Long klijentId;
    private Long trenerId;
    private String status;
    private LocalDate datumPovezivanja;
}
