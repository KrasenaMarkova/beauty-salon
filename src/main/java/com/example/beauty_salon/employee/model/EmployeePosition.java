package com.example.beauty_salon.employee.model;

public enum EmployeePosition {

  HAIRDRESSER("Hairdresser"),
  MANICURE("Manicure"),
  COSMETICIAN("Cosmician");

  private final String displayName;

  EmployeePosition(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
