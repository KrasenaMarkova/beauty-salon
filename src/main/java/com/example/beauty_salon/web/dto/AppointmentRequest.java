package com.example.beauty_salon.web.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

import java.util.UUID;

@Data
public class AppointmentRequest {

    @NotNull(message = "Датата е задължителна.")
    @Future(message = "Не можете да изберете минала дата и час.")
    private LocalDateTime appointmentDate;

    @NotNull(message = "Услугата е задължителна.")
    private UUID treatmentId;

}
