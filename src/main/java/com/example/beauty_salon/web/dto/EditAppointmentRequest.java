package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class EditAppointmentRequest {

  @NotNull(message = "Датата е задължителна.")
  @Future(message = "Не можете да изберете минала дата и час.")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime appointmentDate;

  @NotNull(message = "Услугата е задължителна.")
  private UUID treatmentId;
}
