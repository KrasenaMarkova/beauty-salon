package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

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

