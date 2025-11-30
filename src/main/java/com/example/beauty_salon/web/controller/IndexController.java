package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.web.dto.LoginRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    userService.register(registerRequest);
    redirectAttributes.addFlashAttribute("registrationSuccessful", "Вашата регистрация е успешна");

    return new ModelAndView("redirect:/login");
  }

  @GetMapping("/login")
  public ModelAndView getLoginPage(@RequestParam(name = "loginAttemptMessage", required = false) String message,
      @RequestParam(name = "error", required = false) String errorMessage) {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("login");
    modelAndView.addObject("loginRequest", new LoginRequest());
    modelAndView.addObject("loginAttemptMessage", message);

    if (errorMessage != null) {
      modelAndView.addObject("errorMessage", "Невалидно потребителско име или парола");
    }

    return modelAndView;
  }

  @GetMapping("/home")
  public ModelAndView getHomePage(@AuthenticationPrincipal UserData userData) {

    UserDto user = userService.getById(userData.getUserId());

    List<Appointment> allAppointments = appointmentService.getAllSortedByUser(userData.getUserId());
    List<Appointment> activeAppointments = appointmentService.getActiveAppointments(userData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("home");
    modelAndView.addObject("user", user);
    modelAndView.addObject("allAppointment", allAppointments);
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

}
