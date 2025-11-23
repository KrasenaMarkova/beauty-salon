package com.example.beauty_salon.appointment.repository;

import com.example.beauty_salon.appointment.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

  List<Appointment> findByUserId(UUID userId);

  @Query("""
      SELECT a FROM Appointment a
      WHERE a.status = com.example.beauty_salon.appointment.model.AppointmentStatus.SCHEDULED
      AND a.appointmentDate BETWEEN :now AND :soon
      """)
  List<Appointment> findUpcomingAppointments(LocalDateTime now, LocalDateTime soon);

  List<Appointment> findAllByUserId(UUID userId);
}