package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.LoginRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }


        //TODO: use feign to call userValidation microservice to check if user with mail and username already excists
        //case 1 - app has user - throw UserAlreadyExistsException and handle it in global ExceptionHandler
        //case 2 - no user exists - continue with registration
        userService.register(registerRequest);
        redirectAttributes.addFlashAttribute("registrationSuccessful", "Your registration is successful");

        return new ModelAndView("redirect:/login");
    }


    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "loginAttemptMessage", required = false) String message) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());
        modelAndView.addObject("loginAttemptMessage", message);

        return modelAndView;
    }


    @PostMapping("/login")
    public ModelAndView loginUser(@Valid LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }

        User user = userService.login(loginRequest);

        session.setAttribute("userId", user.getId());

        return new ModelAndView("redirect:/home");
    }


//    @GetMapping("/home")
//    public ModelAndView getHomePage(HttpSession session) {
//
//        UUID userId = (UUID) session.getAttribute("userId");
//        User user = userService.getById(userId);
//
//        List<Appointment> allAppointment = appointmentService.getAllByUserId(userId);
//
//        List<Appointment> activeAppointments = allAppointment.stream()
//            .filter(a -> a.getStatus().name().equals("SCHEDULED"))
//            .toList();
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("home");
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("allAppointment", allAppointment);
//        modelAndView.addObject("activeAppointments", activeAppointments);
//
//        return modelAndView;
//    }

    @GetMapping("/home")
    public ModelAndView getHomePage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        User user = userService.getById(userId);

        List<Appointment> allAppointment = appointmentService.getAllByUserId(userId).stream()
            .sorted(Comparator.comparing(Appointment::getAppointmentDate))
            .toList();

        List<Appointment> activeAppointments = allAppointment.stream()
            .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
            .toList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allAppointment", allAppointment);
        modelAndView.addObject("activeAppointments", activeAppointments);

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
