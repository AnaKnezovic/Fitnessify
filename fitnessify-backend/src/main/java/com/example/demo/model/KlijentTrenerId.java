package com.example.demo.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class KlijentTrenerId implements Serializable {
    private Long klijentId;
    private Long trenerId;
}
