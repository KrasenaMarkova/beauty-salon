package com.example.beauty_salon.appointment.repository;

import com.example.beauty_salon.appointment.model.Appointment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

  List<Appointment> findByUserId(UUID userId);

  List<Appointment> findAllByUserId(UUID userId);

  List<Appointment> findAllByEmployeeId(UUID employeeId);
}