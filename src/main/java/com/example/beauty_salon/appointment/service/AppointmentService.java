package com.example.beauty_salon.appointment.service;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }


    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

}

