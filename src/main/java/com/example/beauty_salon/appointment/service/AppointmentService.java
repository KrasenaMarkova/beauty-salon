package com.example.beauty_salon.appointment.service;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.repository.AppointmentRepository;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final EmployeeService employeeService;
  private final UserService userService;
  private final BeautyTreatmentService beautyTreatmentService;

  @Autowired
  public AppointmentService(AppointmentRepository appointmentRepository, EmployeeService employeeService, UserService userService,
      BeautyTreatmentService beautyTreatmentService) {
    this.appointmentRepository = appointmentRepository;
    this.employeeService = employeeService;
    this.userService = userService;
    this.beautyTreatmentService = beautyTreatmentService;
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

  public List<Appointment> getAllByUserId(UUID userId) {
    return appointmentRepository.findByUserId(userId);
  }

  public Appointment getById(UUID appointmentId) {
    return appointmentRepository.getById(appointmentId);
  }

  @Transactional
  public void cancelAppointment(UUID appointmentId) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Часът не съществува!"));

    appointment.setStatus(AppointmentStatus.CANCELLED);
    appointmentRepository.save(appointment);
  }

  public List<Appointment> getAllSortedByUser(UUID userId) {
    return appointmentRepository.findAllByUserId(userId).stream()
        .sorted(Comparator.comparing(Appointment::getAppointmentDate))
        .toList();
  }

  public List<Appointment> getActiveAppointments(UUID userId) {
    return appointmentRepository.findAllByUserId(userId).stream()
        .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
        .sorted(Comparator.comparing(Appointment::getAppointmentDate))
        .toList();
  }

  public List<Appointment> getPastAppointmentsForUser(UUID userId) {
    List<Appointment> allAppointments = getAllByUserId(userId);

    if (allAppointments == null) {
      return allAppointments = new ArrayList<>();
    }

    return allAppointments.stream()
        .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
            || a.getStatus() == AppointmentStatus.CANCELLED)
        .sorted(
            Comparator.comparing((Appointment a) -> a.getAppointmentDate().toLocalDate()).reversed()
                .thenComparing(Appointment::getAppointmentDate)
        )
        .toList();
  }

  public void deleteAppointment(UUID appointmentId, UserData userData) {

    if (userData == null || userData.getUserId() == null) {
      throw new SecurityException("User is not logged in.");
    }

    deleteAppointmentForUser(appointmentId, userData.getUserId());

  }

  public void deleteAppointmentForUser(UUID appointmentId, UUID userId) {

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

    if (!appointment.getUser().getId().equals(userId)) {
      throw new SecurityException("User does not own this appointment");
    }

    appointmentRepository.deleteById(appointmentId);
  }

  public void editAppointmentForUser(UUID appointmentId, UUID userId, EditAppointmentRequest editAppointmentRequest) {
    Appointment existing = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Часът не съществува."));

    if (!existing.getUser().getId().equals(userId)) {
      throw new SecurityException("Нямате права да редактирате този час.");
    }

    existing.setAppointmentDate(editAppointmentRequest.getAppointmentDate());

    if (editAppointmentRequest.getTreatmentId() != null) {
      BeautyTreatment treatment = beautyTreatmentService.getById(editAppointmentRequest.getTreatmentId());
      existing.setTreatment(treatment);
      existing.setPrice(treatment.getPrice());
      existing.setDurationMinutes(treatment.getDurationMinutes());
    }

    appointmentRepository.save(existing);
  }

  public EditAppointmentRequest prepareEditForm(UUID appointmentId, UUID userId) {
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Този час не съществува."));

    if (!appointment.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("Нямате права да редактирате този час.");
    }

    if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
        appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Този час не може да бъде редактиран.");
    }

    EditAppointmentRequest editAppointmentRequest = new EditAppointmentRequest();
    editAppointmentRequest.setAppointmentDate(appointment.getAppointmentDate());

    if (appointment.getTreatment() != null) {
      editAppointmentRequest.setTreatmentId(appointment.getTreatment().getId());
    }

    return editAppointmentRequest;
  }

}

