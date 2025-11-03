package com.example.beauty_salon.beautyService.model;

public enum ServiceName {
    HAIRCUT("Haircut"),
    MANICURE("Manicure"),
    FACIAL_CLEANSING("Facial Cleansing");

    private final String displayName;

    ServiceName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

