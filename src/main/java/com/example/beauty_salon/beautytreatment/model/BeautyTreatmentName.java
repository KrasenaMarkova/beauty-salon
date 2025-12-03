package com.example.beauty_salon.beautytreatment.model;

import lombok.Getter;

@Getter
public enum BeautyTreatmentName {
  HAIRCUT("Haircut"),
  MANICURE("Manicure"),
  FACIAL_CLEANSING("Facial Cleansing");

  private final String displayName;

  BeautyTreatmentName(String displayName) {
    this.displayName = displayName;
  }
}

