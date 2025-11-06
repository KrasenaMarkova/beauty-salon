package com.example.beauty_salon.web.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AppointmentRequest {

    // Приема датата от календара
    @NotNull(message = "Датата е задължителна.")
    private LocalDate date;

    // Приема избрания час (HH:mm)
    @NotNull(message = "Часът е задължителен.")
    private LocalTime selectedTime;

    // UUID на избраната услуга
    @NotNull(message = "Услугата е задължителна.")
    private UUID serviceId;

    // UUID на потребителя, който прави резервацията
    // Обикновено се взима от Spring Security Context, но за формата го дефинираме тук:
    @NotNull(message = "Потребителят е задължителен.")
    private UUID userId;

//    @NotNull
//    private ServiceName serviceName;
//
//    @Future
//    @NotNull
//    private LocalDateTime appointmentDate;
}
