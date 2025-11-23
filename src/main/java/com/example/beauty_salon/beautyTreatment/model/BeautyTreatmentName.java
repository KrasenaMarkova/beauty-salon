package com.example.beauty_salon.beautyTreatment.model;

public enum BeautyTreatmentName {
  HAIRCUT("Haircut"),
  MANICURE("Manicure"),
  FACIAL_CLEANSING("Facial Cleansing");

  private final String displayName;

  BeautyTreatmentName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}

