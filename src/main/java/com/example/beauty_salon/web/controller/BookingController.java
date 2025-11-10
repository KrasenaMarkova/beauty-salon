package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.web.dto.AppointmentRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookingController {
    private final AppointmentService appointmentService;
    private final BeautyTreatmentRepository beautyTreatmentRepository;

    public BookingController(AppointmentService appointmentService, BeautyTreatmentRepository beautyTreatmentRepository) {
        this.appointmentService = appointmentService;
        this.beautyTreatmentRepository = beautyTreatmentRepository;
    }

    @GetMapping("/booking")
    public ModelAndView showBookingForm() {

        ModelAndView modelAndView = new ModelAndView("booking");

        modelAndView.addObject("treatments", beautyTreatmentRepository.findAll());
        modelAndView.addObject("bookingForm", new AppointmentRequest());

        return modelAndView;
    }

}