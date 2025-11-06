package com.example.beauty_salon.appointment.repository;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByUserIdOrderByAppointmentDateDesc(UUID userId);

    // Извлича всички запазени часове за даден ден с определен статус
    List<Appointment> findByAppointmentDateBetweenAndStatus(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            AppointmentStatus status
    );

    List<Appointment> findByAppointmentDateBetweenAndStatusAndEmployeeId(
            LocalDateTime localDateTime,
            LocalDateTime localDateTime1,
            AppointmentStatus appointmentStatus,
            UUID employeeId);
}