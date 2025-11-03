package com.example.beauty_salon.appointment.model;

public enum AppointmentStatus {

    SCHEDULED("Scheduled"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

