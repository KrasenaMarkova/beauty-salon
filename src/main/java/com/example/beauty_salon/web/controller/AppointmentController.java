package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;
  private final UserService userService;
  private final BeautyTreatmentService beautyTreatmentService;

  @PostMapping("/{id}/cancel")
  public String cancelAppointment(@PathVariable("id") UUID appointmentId, @AuthenticationPrincipal UserData userData,
      RedirectAttributes redirectAttributes) {

    appointmentService.cancelAppointment(appointmentId);
    redirectAttributes.addFlashAttribute("successMessage", "Часът е успешно отменен.");

    return "redirect:/home";
  }

  @GetMapping("/{id}/edit")
  public ModelAndView showEditForm(@PathVariable("id") UUID appointmentId, @AuthenticationPrincipal UserData userData) {

    EditAppointmentRequest editAppointmentRequest =
        appointmentService.prepareEditForm(appointmentId, userData.getUserId());

    ModelAndView modelAndView = new ModelAndView("appointment-edit");
    modelAndView.addObject("editAppointmentRequest", editAppointmentRequest);
    modelAndView.addObject("appointment", appointmentService.getById(appointmentId));
    modelAndView.addObject("treatments", beautyTreatmentService.getAll());

    return modelAndView;
  }

  @PostMapping("/{id}/edit")
  public ModelAndView editAppointment(@PathVariable("id") UUID appointmentId,
      @ModelAttribute("editAppointmentRequest") EditAppointmentRequest editAppointmentRequest,
      @AuthenticationPrincipal UserData userData, RedirectAttributes redirectAttributes,
      @RequestParam(name = "error", required = false) String errorMessage) {

    if (userData == null || userData.getUserId() == null) {
      return new ModelAndView("redirect:/login");
    }

    appointmentService.editAppointmentForUser(appointmentId, userData.getUserId(), editAppointmentRequest);
    ModelAndView modelAndView = new ModelAndView("redirect:/home");
    redirectAttributes.addFlashAttribute("successMessage", "Часът беше успешно редактиран.");

    if (errorMessage != null) {
      modelAndView.addObject("errorMessage", "Невалидно потребителско име или парола");
    }

    return modelAndView;
  }

  @PostMapping("/{id}/delete")
  public ModelAndView deleteAppointment(@PathVariable UUID id, @AuthenticationPrincipal UserData userData,
      RedirectAttributes redirectAttributes) {

    if (userData == null || userData.getUserId() == null) {
      return new ModelAndView("redirect:/login");
    }

    appointmentService.deleteAppointmentForUser(id, userData.getUserId());

    ModelAndView modelAndView = new ModelAndView("redirect:/appointments/history");
    redirectAttributes.addFlashAttribute("successMessage", "Часът беше изтрит успешно.");

    return modelAndView;
  }

  @GetMapping("/history")
  public ModelAndView getAppointmentHistoryPage(@AuthenticationPrincipal UserData userData) {

    if (userData == null || userData.getUserId() == null) {
      return new ModelAndView("redirect:/login");
    }

    UserDto user = userService.getById(userData.getUserId());
    List<Appointment> pastAppointments = appointmentService.getPastAppointmentsForUser(userData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("appointments-history");
    modelAndView.addObject("user", user);
    modelAndView.addObject("pastAppointments", pastAppointments);

    return modelAndView;
  }
}

