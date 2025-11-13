package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.web.dto.AppointmentRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookingController {
    private final AppointmentService appointmentService;
    private final BeautyTreatmentRepository beautyTreatmentRepository;
    private final EmployeeRepository employeeRepository;

    public BookingController(AppointmentService appointmentService, BeautyTreatmentRepository beautyTreatmentRepository,
        EmployeeRepository employeeRepository) {
        this.appointmentService = appointmentService;
        this.beautyTreatmentRepository = beautyTreatmentRepository;
      this.employeeRepository = employeeRepository;
    }

//    @GetMapping("/booking")
//    public ModelAndView showBookingForm() {
//
//        ModelAndView modelAndView = new ModelAndView("booking");
//
//        modelAndView.addObject("treatments", beautyTreatmentRepository.findAll());
//        modelAndView.addObject("bookingForm", new AppointmentRequest());
//
//        return modelAndView;
//    }

    @GetMapping("/booking")
    public ModelAndView showBookingForm() {
        ModelAndView modelAndView = new ModelAndView("booking");
        modelAndView.addObject("treatments", beautyTreatmentRepository.findAll());
        // Ако служителят се избира автоматично, не е нужно да ги пращаш
        // modelAndView.addObject("employees", employeeRepository.findAll());
        modelAndView.addObject("appointmentRequest", new AppointmentRequest()); //

        return modelAndView;
    }

    @PostMapping("/booking")
    public ModelAndView bookAppointment(@Valid @ModelAttribute("appointmentRequest") AppointmentRequest appointmentRequest,
        BindingResult result, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView("booking");
        modelAndView.addObject("treatments", beautyTreatmentRepository.findAll());

        // ❗️ employees не са нужни, ако системата автоматично избира служител
        // modelAndView.addObject("employees", employeeRepository.findAll());

        if (result.hasErrors()) {
            modelAndView.addObject("error", "Моля, попълнете коректно всички задължителни полета.");
            return modelAndView;
        }

        try {
            // ✅ вземаме userId от HttpSession
            UUID userId = (UUID) session.getAttribute("userId");

            if (userId == null) {
                modelAndView.setViewName("redirect:/login");
                return modelAndView;
            }

            // създаваме запазения час
            appointmentService.createAppointment(
                userId,
                appointmentRequest.getTreatmentId(),
                appointmentRequest.getAppointmentDate()
            );

            modelAndView.addObject("success", "Часът беше успешно запазен!");
            modelAndView.addObject("appointmentRequest", new AppointmentRequest());
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }

        return modelAndView;
    }

}