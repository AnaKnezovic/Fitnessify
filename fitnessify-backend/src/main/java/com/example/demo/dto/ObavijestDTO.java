package com.example.demo.dto;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ObavijestDTO {
    private Long korisnikId;
    private String tekst;
    private LocalDateTime vrijeme;
}
