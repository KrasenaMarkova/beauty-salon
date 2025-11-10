package com.example.beauty_salon.web.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AppointmentRequest {

    @NotNull(message = "Датата е задължителна.")
    private LocalDate date;

    @NotNull(message = "Часът е задължителен.")
    private LocalTime selectedTime;

    @NotNull(message = "Услугата е задължителна.")
    private UUID serviceId;

    @NotNull(message = "Потребителят е задължителен.")
    private UUID userId;

//    @NotNull
//    private ServiceName serviceName;
//
//    @Future
//    @NotNull
//    private LocalDateTime appointmentDate;
}
