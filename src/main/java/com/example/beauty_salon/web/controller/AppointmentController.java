package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

  private final AppointmentService appointmentService;
  private final UserService userService;
  private final BeautyTreatmentService beautyTreatmentService;


  public AppointmentController(AppointmentService appointmentService, UserService userService, BeautyTreatmentService beautyTreatmentService) {
    this.appointmentService = appointmentService;
    this.userService = userService;
    this.beautyTreatmentService = beautyTreatmentService;
  }

//  @PostMapping("/{id}/cancel")
//  public String cancelAppointment(@PathVariable("id") UUID appointmentId, @AuthenticationPrincipal UserData userData, RedirectAttributes redirectAttributes) {
//    UUID userId = userData.getUserId();
//    Appointment appointment = appointmentService.getById(appointmentId);
//
//    appointment.setStatus(CANCELLED);
//    appointmentService.save(appointment);
//
//    redirectAttributes.addFlashAttribute("successMessage", "Часът е успешно отменен.");
//    return "redirect:/home";
//  }

  @PostMapping("/{id}/cancel")
  public String cancelAppointment(@PathVariable("id") UUID appointmentId, @AuthenticationPrincipal UserData userData,
      RedirectAttributes redirectAttributes) {

    // TODO try - catch махни от контролера

    try {
      appointmentService.cancelAppointment(appointmentId);
      redirectAttributes.addFlashAttribute("successMessage", "Часът е успешно отменен.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }

    return "redirect:/home";
  }

  @GetMapping("/{id}/edit")
  public ModelAndView showEditForm(@PathVariable("id") UUID appointmentId, @AuthenticationPrincipal UserData userData) {
//    UUID userId = userData.getUserId();
//    Appointment appointment = appointmentService.getById(appointmentId);
//
//    if (appointment == null || !appointment.getUser().getId().equals(userId) ||
//        appointment.getStatus() == AppointmentStatus.CANCELLED ||
//        appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
//      ModelAndView mv = new ModelAndView("redirect:/home");
//      mv.addObject("errorMessage", "Този час не може да бъде редактиран или не съществува.");
//      return mv;
//    }
//
//    EditAppointmentRequest editAppointmentRequest = new EditAppointmentRequest();
//    editAppointmentRequest.setAppointmentDate(appointment.getAppointmentDate());
//    if (appointment.getTreatment() != null) {
//      editAppointmentRequest.setTreatmentId(appointment.getTreatment().getId());
//    }

    EditAppointmentRequest editAppointmentRequest =
        appointmentService.prepareEditForm(appointmentId, userData.getUserId());

    ModelAndView modelAndView = new ModelAndView("appointment-edit");
    modelAndView.addObject("editAppointmentRequest", editAppointmentRequest);
//    modelAndView.addObject("appointment", appointment);
    modelAndView.addObject("appointment", appointmentService.getById(appointmentId));
    modelAndView.addObject("treatments", beautyTreatmentService.getAll());
    return modelAndView;
  }

  @PostMapping("/{id}/edit")
  public ModelAndView editAppointment(@PathVariable("id") UUID appointmentId,
      @ModelAttribute("editAppointmentRequest") EditAppointmentRequest editAppointmentRequest,
      @AuthenticationPrincipal UserData userData) {

//    UUID userId = userData.getUserId();
//    Appointment existing = appointmentService.getById(appointmentId);
//
//    if (existing == null || !existing.getUser().getId().equals(userId)) {
//      ModelAndView mv = new ModelAndView("redirect:/home");
//      mv.addObject("errorMessage", "Нямате права или часът не съществува.");
//      return mv;
//    }
//
//    existing.setAppointmentDate(editAppointmentRequest.getAppointmentDate());
//
//    if (editAppointmentRequest.getTreatmentId() != null) {
//      BeautyTreatment treatment = beautyTreatmentService.getById(editAppointmentRequest.getTreatmentId());
//      existing.setTreatment(treatment);
//      existing.setPrice(treatment.getPrice());
//      existing.setDurationMinutes(treatment.getDurationMinutes());
//    }
//
//    appointmentService.save(existing);
    if (userData == null || userData.getUserId() == null) {
      return new ModelAndView("redirect:/login");
    }

    ModelAndView modelAndView = new ModelAndView("redirect:/home");
//    modelAndView.addObject("successMessage", "Часът беше успешно редактиран.");

    try {
      appointmentService.editAppointmentForUser(appointmentId, userData.getUserId(), editAppointmentRequest);
      modelAndView.addObject("successMessage", "Часът беше успешно редактиран.");
    } catch (SecurityException | IllegalArgumentException e) {
      modelAndView.addObject("errorMessage", e.getMessage());
    }
    return modelAndView;
  }

  @PostMapping("/{id}/delete")
  public ModelAndView deleteAppointment(@PathVariable UUID id, @AuthenticationPrincipal UserData userData) {
//    UUID userId = userData.getUserId();
//    if (userId == null) {
//      return new ModelAndView("redirect:/login");
//    }
//
//    Appointment appointment = appointmentService.getById(id);
//
//    if (appointment != null && appointment.getUser().getId().equals(userId)) {
//      appointmentService.deleteAppointment(id);
//    }

    if (userData == null || userData.getUserId() == null) {
      return new ModelAndView("redirect:/login");
    }

//    appointmentService.deleteAppointmentForUser(id, userData.getUserId());

    // TODO try - catch махни от контролера

    try {
      appointmentService.deleteAppointmentForUser(id, userData.getUserId());
    } catch (Exception e) {
      System.out.println("Delete failed: " + e.getMessage());
    }
    return new ModelAndView("redirect:/appointments/history");
  }

  @GetMapping("/history")
  public ModelAndView getAppointmentHistoryPage(@AuthenticationPrincipal UserData userData) {
//    UUID userId = userData.getUserId();
//    if (userId == null) {
//      return new ModelAndView("redirect:/login");
//    }
//
//    User user = userService.getById(userId);
//
//    List<Appointment> allAppointments = appointmentService.getAllByUserId(userId);
//    if (allAppointments == null) {
//      allAppointments = new ArrayList<>();
//    }
//
//    List<Appointment> pastAppointments = allAppointments.stream()
//        .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
//            || a.getStatus() == AppointmentStatus.CANCELLED)
//        .sorted(
//            Comparator.comparing((Appointment a) -> a.getAppointmentDate().toLocalDate()).reversed()
//                .thenComparing(Appointment::getAppointmentDate)
//        )
//        .toList();

    if (userData == null || userData.getUserId() == null) {
      return new ModelAndView("redirect:/login");
    }

    User user = userService.getById(userData.getUserId());
    List<Appointment> pastAppointments = appointmentService.getPastAppointmentsForUser(userData.getUserId());

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("appointments-history");
    modelAndView.addObject("user", user);
    modelAndView.addObject("pastAppointments", pastAppointments);

    return modelAndView;
  }

}

