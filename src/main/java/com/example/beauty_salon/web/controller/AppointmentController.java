package com.example.beauty_salon.web.controller;

import static com.example.beauty_salon.appointment.model.AppointmentStatus.CANCELLED;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.repository.UserRepository;
import com.example.beauty_salon.user.service.UserService;
import jakarta.servlet.http.HttpSession;
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
    private final UserRepository userRepository;
    private final BeautyTreatmentRepository beautyTreatmentRepository;
    private final EmployeeRepository employeeRepository;


    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService, UserRepository userRepository, BeautyTreatmentRepository beautyTreatmentRepository, EmployeeRepository employeeRepository) {
        this.appointmentService = appointmentService;
        this.userService = userService;
      this.userRepository = userRepository;
      this.beautyTreatmentRepository = beautyTreatmentRepository;
      this.employeeRepository = employeeRepository;
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
            .sorted(Comparator.comparing(Appointment::getAppointmentDate).reversed())
            .toList();

        List<Appointment> activeAppointments = allAppointments.stream()
            .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
            .sorted(Comparator.comparing(Appointment::getAppointmentDate))
            .toList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("appointments-history");
        modelAndView.addObject("user", user);
        modelAndView.addObject("pastAppointments", pastAppointments);
        modelAndView.addObject("allAppointment", activeAppointments);

        return modelAndView;
    }

}

