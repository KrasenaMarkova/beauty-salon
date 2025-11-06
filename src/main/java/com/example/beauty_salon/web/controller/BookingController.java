package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyService.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.web.dto.AppointmentRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        ModelAndView modelAndView = new ModelAndView("bookingForm");

        modelAndView.addObject("treatments", beautyTreatmentRepository.findAll());
        modelAndView.addObject("bookingForm", new AppointmentRequest());

        return modelAndView;
    }

}