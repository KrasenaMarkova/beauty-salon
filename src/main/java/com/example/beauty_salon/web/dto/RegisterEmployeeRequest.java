package com.example.beauty_salon.web.dto;

import com.example.beauty_salon.employee.model.EmployeePosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterEmployeeRequest {

  @NotBlank(message = "Името е задължително.")
  private String name;

  @NotNull(message = "Изберете позиция.")
  private EmployeePosition employeePosition;
}
