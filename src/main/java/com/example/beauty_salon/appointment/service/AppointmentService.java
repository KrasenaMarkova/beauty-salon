package com.example.beauty_salon.appointment.service;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.repository.AppointmentRepository;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.email.EmailService;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.repository.UserRepository;
import com.example.beauty_salon.user.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmployeeService employeeService;
    private final UserService userService;
    private final BeautyTreatmentService beautyTreatmentService;
    private final EmailService emailService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, EmployeeService employeeService, UserService userService,
                            BeautyTreatmentService beautyTreatmentService, EmailService emailService) {
      this.appointmentRepository = appointmentRepository;
      this.employeeService = employeeService;
      this.userService = userService;
      this.beautyTreatmentService = beautyTreatmentService;
      this.emailService = emailService;
    }

    @Transactional
    public Appointment createAppointment(UUID userId, UUID treatmentId, LocalDateTime dateTime) {
        User user = userService.getById(userId);

        BeautyTreatment treatment = beautyTreatmentService.getById(treatmentId);

        EmployeePosition requiredPosition = mapTreatmentToEmployeePosition(treatment.getBeautyTreatmentName());

        List<Employee> potentialEmployees = employeeService.getEmployeeByPosition(requiredPosition);
        if (potentialEmployees.isEmpty()) {
            throw new IllegalStateException("Няма наличен служител за тази услуга.");
        }

        Employee employee = potentialEmployees.stream()
            .filter(e -> isEmployeeAvailable(e, dateTime, treatment.getDurationMinutes()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Няма свободен служител за избрания час."));

        Appointment appointment = Appointment.builder()
            .appointmentDate(dateTime)
            .durationMinutes(treatment.getDurationMinutes())
            .price(treatment.getPrice())
            .status(AppointmentStatus.SCHEDULED)
            .user(user)
            .employee(employee)
            .treatment(treatment)
            .build();

        return appointmentRepository.save(appointment);
    }

    private boolean isEmployeeAvailable(Employee employee, LocalDateTime startTime, int durationMinutes) {

        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(18, 0);

        LocalDate appointmentDate = startTime.toLocalDate();
        LocalDateTime dayStart = appointmentDate.atTime(workStart);
        LocalDateTime dayEnd = appointmentDate.atTime(workEnd);

        List<Appointment> existing = appointmentRepository.findAll().stream()
            .filter(a -> a.getEmployee().getId().equals(employee.getId()))
            .filter(a -> !a.getAppointmentDate().isBefore(dayStart) && !a.getAppointmentDate().isAfter(dayEnd))
            .toList();

        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        return existing.stream().noneMatch(a -> {
            LocalDateTime aStart = a.getAppointmentDate();
            LocalDateTime aEnd = aStart.plusMinutes(a.getDurationMinutes());
            return aStart.isBefore(endTime) && aEnd.isAfter(startTime);
        }) && !startTime.toLocalTime().isBefore(workStart) && !endTime.toLocalTime().isAfter(workEnd);
    }

    private EmployeePosition mapTreatmentToEmployeePosition(BeautyTreatmentName treatmentName) {
        return switch (treatmentName) {
            case HAIRCUT -> EmployeePosition.HAIRDRESSER;
            case MANICURE -> EmployeePosition.MANICURE;
            case FACIAL_CLEANSING -> EmployeePosition.COSMETICIAN;
        };
    }

    @Transactional
    public void markPastAppointmentsAsCompleted() {
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> pastAppointments = appointmentRepository.findAll().stream()
            .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
            .filter(a -> a.getAppointmentDate().plusMinutes(a.getDurationMinutes()).isBefore(now))
            .toList();

        pastAppointments.forEach(a -> a.setStatus(AppointmentStatus.COMPLETED));
        appointmentRepository.saveAll(pastAppointments);
    }

//    public void sendUpcomingAppointmentReminders() {
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime soon = now.plusHours(2);
//
//        List<Appointment> upcoming =
//            appointmentRepository.findUpcomingAppointments(now, soon);
//
//        for (Appointment appointment : upcoming) {
//            BeautyTreatment treatment = appointment.getTreatment();
//
//            if (treatment != null) {
//                emailService.sendAppointmentReminder(
//                    appointment.getUser().getEmail(),
//                    treatment.getBeautyTreatmentName().getDisplayName(),
//                    appointment.getAppointmentDate()
//                );
//            }
//        }
//
//        System.out.println("Sent reminders: " + upcoming.size());
//    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAllByUserId(UUID userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public Appointment getById(UUID appointmentId) {
        return appointmentRepository.getById(appointmentId);
    }

    public void save(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }

}

