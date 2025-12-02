package com.example.beauty_salon.employee.model;

import lombok.Getter;

@Getter
public enum EmployeePosition {

  HAIRDRESSER("Hairdresser"),
  MANICURE("Manicure"),
  COSMETICIAN("Cosmician");

  private final String displayName;

  EmployeePosition(String displayName) {
    this.displayName = displayName;
  }
}
