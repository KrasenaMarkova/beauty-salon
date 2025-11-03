package com.example.beauty_salon.beautyService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BeautyService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceName serviceName;

    @Column(nullable = false)
    //@Size(min = 3, max = 50)
    private String serviceDescription;

    @Column(nullable = false)
    @Min(value = 0)
    private BigDecimal price;

    private int durationMinutes;
}
