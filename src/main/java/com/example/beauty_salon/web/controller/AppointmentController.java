package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyService.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.AppointmentRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/history")
    public ModelAndView getAppointmentHistoryPage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return new ModelAndView("redirect:/login"); // или друга страница
        }

        User user = userService.getById(userId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("appointments-history");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

}

