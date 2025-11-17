package com.example.beauty_salon.web.controller;

import static com.example.beauty_salon.appointment.model.AppointmentStatus.CANCELLED;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.repository.UserRepository;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final BeautyTreatmentService beautyTreatmentService;


    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService, UserRepository userRepository, BeautyTreatmentRepository beautyTreatmentRepository, EmployeeRepository employeeRepository,
        BeautyTreatmentService beautyTreatmentService, EmployeeService employeeService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
      this.employeeService = employeeService;
      this.beautyTreatmentService = beautyTreatmentService;
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") UUID appointmentId, HttpSession session, RedirectAttributes redirectAttributes) {
        UUID userId = (UUID) session.getAttribute("userId");
        Appointment appointment = appointmentService.getById(appointmentId);

        appointment.setStatus(CANCELLED); // или CANCELLED
        appointmentService.save(appointment);

        redirectAttributes.addFlashAttribute("successMessage", "Часът е успешно отменен.");
        return "redirect:/home";
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditForm(@PathVariable("id") UUID appointmentId, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        Appointment appointment = appointmentService.getById(appointmentId);

        if (appointment == null || !appointment.getUser().getId().equals(userId) ||
            appointment.getStatus() == AppointmentStatus.CANCELLED ||
            appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("errorMessage", "Този час не може да бъде редактиран или не съществува.");
            return mv;
        }

        EditAppointmentRequest editAppointmentRequest = new EditAppointmentRequest();
        editAppointmentRequest.setAppointmentDate(appointment.getAppointmentDate());
        if (appointment.getTreatment() != null) {
            editAppointmentRequest.setTreatmentId(appointment.getTreatment().getId());
        }

        ModelAndView modelAndView = new ModelAndView("appointment-edit");
        modelAndView.addObject("editAppointmentRequest", editAppointmentRequest); // същото име, каквото ползваш във form
        modelAndView.addObject("appointment", appointment); // ако искаш за справка в HTML
        modelAndView.addObject("treatments", beautyTreatmentService.getAll());
        return modelAndView;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView editAppointment(@PathVariable("id") UUID appointmentId,
        @ModelAttribute("editAppointmentRequest") EditAppointmentRequest editAppointmentRequest,
        HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        Appointment existing = appointmentService.getById(appointmentId);

        if (existing == null || !existing.getUser().getId().equals(userId)) {
            ModelAndView mv = new ModelAndView("redirect:/home");
            mv.addObject("errorMessage", "Нямате права или часът не съществува.");
            return mv;
        }

        existing.setAppointmentDate(editAppointmentRequest.getAppointmentDate());

        if (editAppointmentRequest.getTreatmentId() != null) {
            BeautyTreatment treatment = beautyTreatmentService.getById(editAppointmentRequest.getTreatmentId());
            existing.setTreatment(treatment);
            existing.setPrice(treatment.getPrice());
            existing.setDurationMinutes(treatment.getDurationMinutes());
        }

        appointmentService.save(existing);

        ModelAndView mv = new ModelAndView("redirect:/home");
        mv.addObject("successMessage", "Часът беше успешно редактиран.");
        return mv;
    }

//    @GetMapping("/history")
//    public ModelAndView getAppointmentHistoryPage(HttpSession session) {
//        UUID userId = (UUID) session.getAttribute("userId");
//        if (userId == null) {
//            return new ModelAndView("redirect:/login");
//        }
//
//        User user = userService.getById(userId);
//
//        List<Appointment> allAppointments = appointmentService.getAllByUserId(userId);
//        if (allAppointments == null) {
//            allAppointments = new ArrayList<>();
//        }
//
//        List<Appointment> pastAppointments = allAppointments.stream()
//            .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
//                || a.getStatus() == AppointmentStatus.CANCELLED)
//            .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
//            .toList();
//
//        List<Appointment> activeAppointments = allAppointments.stream()
//            .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
//            .sorted(Comparator.comparing(Appointment::getAppointmentDate))
//            .toList();
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("appointments-history");
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("pastAppointments", pastAppointments);
//        modelAndView.addObject("allAppointment", activeAppointments);
//
//        return modelAndView;
//    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteAppointment(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        Appointment appointment = appointmentService.getById(id);

        if (appointment != null && appointment.getUser().getId().equals(userId)) {
            appointmentService.deleteAppointment(id);
        }

        return new ModelAndView("redirect:/appointments/history");
    }

    @GetMapping("/history")
    public ModelAndView getAppointmentHistoryPage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getById(userId);

        List<Appointment> allAppointments = appointmentService.getAllByUserId(userId);
        if (allAppointments == null) {
            allAppointments = new ArrayList<>();
        }

        List<Appointment> pastAppointments = allAppointments.stream()
            .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
                || a.getStatus() == AppointmentStatus.CANCELLED)
            .sorted(
                Comparator.comparing((Appointment a) -> a.getAppointmentDate().toLocalDate()).reversed() // дата низходящо
                    .thenComparing(Appointment::getAppointmentDate) // час възходящо
            )
            .toList();

//        List<Appointment> activeAppointments = allAppointments.stream()
//            .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
//            .sorted(Comparator.comparing(Appointment::getAppointmentDate))
//            .toList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("appointments-history");
        modelAndView.addObject("user", user);
        modelAndView.addObject("pastAppointments", pastAppointments);
//        modelAndView.addObject("allAppointment", activeAppointments);

        return modelAndView;
    }

}

