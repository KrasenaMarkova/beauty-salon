package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.web.dto.AppointmentRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookingController {

  private final AppointmentService appointmentService;
  private final BeautyTreatmentService beautyTreatmentService;

  @Autowired
  public BookingController(AppointmentService appointmentService, BeautyTreatmentService beautyTreatmentService) {
    this.appointmentService = appointmentService;
    this.beautyTreatmentService = beautyTreatmentService;
  }

  @GetMapping("/booking")
  public ModelAndView showBookingForm() {

    ModelAndView modelAndView = new ModelAndView("booking");
    modelAndView.addObject("treatments", beautyTreatmentService.getAll());
    modelAndView.addObject("appointmentRequest", new AppointmentRequest());

    return modelAndView;
  }

  @PostMapping("/booking")
  @PreAuthorize("isAuthenticated()")
  public ModelAndView bookAppointment(@Valid @ModelAttribute("appointmentRequest") AppointmentRequest appointmentRequest,
      BindingResult result, @AuthenticationPrincipal UserData userData) {

    ModelAndView modelAndView = new ModelAndView("booking");
    modelAndView.addObject("treatments", beautyTreatmentService.getAll());

    if (result.hasErrors()) {
      modelAndView.addObject("error", "Моля, попълнете коректно всички задължителни полета.");
      return modelAndView;
    }

    appointmentService.createAppointment(
        userData.getUserId(),
        appointmentRequest.getTreatmentId(),
        appointmentRequest.getAppointmentDate());

    modelAndView.addObject("success", "Часът беше успешно запазен!");
    modelAndView.addObject("appointmentRequest", new AppointmentRequest());

    return modelAndView;
  }
}