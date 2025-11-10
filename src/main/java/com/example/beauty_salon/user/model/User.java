package com.example.beauty_salon.user.model;

import com.example.beauty_salon.appointment.model.Appointment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    private boolean active;

    // Един потребител може да има много часове (appointments)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @OrderBy("appointmentDate DESC")
    private List<Appointment> appointments = new ArrayList<>();
}