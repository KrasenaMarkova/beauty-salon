package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.LoginRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;
    private final AppointmentService appointmentService;

    @Autowired
    public IndexController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }


    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.register(registerRequest);

        return "redirect:/login";
    }


    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        //public ModelAndView getLoginPage(@RequestParam(name = "loginAttemptMessage", required = false) String message) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

//        ??????
//        modelAndView.addObject("loginAttemptMessage", message);

        return modelAndView;
    }

    // Autowire HttpSession = automatically create user session, generate session id and return Set-Cookie header with the session id
    @PostMapping("/login")
    public String loginUser(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "login";
        }

        User user = userService.login(loginRequest);
        session.setAttribute("userId", user.getId());

        return "redirect:/home";
    }


    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        List<Appointment> allAppointment = appointmentService.getAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allAppointment", allAppointment);

        return modelAndView;
    }


    @GetMapping("/about-us")
    public String about() {
        return "about-us";
    }

    @GetMapping("/team")
    public String team() {
        return "team";
    }

    @GetMapping("/logout")
    public String getLogout(HttpSession session) {

        session.invalidate();
        return "redirect:/";
    }
}
