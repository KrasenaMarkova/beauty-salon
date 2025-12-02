package com.example.beauty_salon.appointment.service;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.repository.AppointmentRepository;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.exception.NoFreeEmployeeException;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final EmployeeService employeeService;
  private final UserService userService;
  private final BeautyTreatmentService beautyTreatmentService;

  @Transactional
  public Appointment createAppointment(UUID userId, UUID treatmentId, LocalDateTime dateTime) {
    UserDto user = userService.getById(userId);

    BeautyTreatment treatment = beautyTreatmentService.getById(treatmentId);

    EmployeePosition requiredPosition = mapTreatmentToEmployeePosition(treatment.getBeautyTreatmentName());

    List<Employee> potentialEmployees = employeeService.getEmployeeByPosition(requiredPosition);
    if (potentialEmployees.isEmpty()) {
      throw new IllegalArgumentException("Няма наличен служител за тази услуга.");
    }

    Employee employee = potentialEmployees.stream()
        .filter(e -> isEmployeeAvailable(e, dateTime, treatment.getDurationMinutes()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Няма свободен служител за избрания час."));

    Appointment appointment = Appointment.builder()
        .appointmentDate(dateTime)
        .durationMinutes(treatment.getDurationMinutes())
        .price(treatment.getPrice())
        .status(AppointmentStatus.SCHEDULED)
        .userId(user.getId())
        .employee(employee)
        .treatment(treatment)
        .build();

    return appointmentRepository.save(appointment);
  }

  private boolean isEmployeeAvailable(Employee employee, LocalDateTime startTime, int durationMinutes) {

    LocalTime start = startTime.toLocalTime();
    LocalTime end = startTime.plusMinutes(durationMinutes).toLocalTime();

    if (start.isBefore(LocalTime.of(9, 0)) ||
        end.isAfter(LocalTime.of(18, 0))) {
      return false;
    }

    List<Appointment> appointments = appointmentRepository.findAllByEmployeeId(employee.getId());
    LocalDateTime requestedEnd = startTime.plusMinutes(durationMinutes);

    for (Appointment a : appointments) {
      LocalDateTime existingStart = a.getAppointmentDate();
      LocalDateTime existingEnd = a.getAppointmentDate().plusMinutes(a.getDurationMinutes());

      boolean overlap =
          startTime.isBefore(existingEnd) &&
              requestedEnd.isAfter(existingStart);

      if (overlap) {
        return false;
      }
    }

    return true;
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

    if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
      throw new IllegalArgumentException("Този час вече е отменен.");
    }

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

//    if (allAppointments == null) {
//      return allAppointments = new ArrayList<>();
//    }

    return allAppointments.stream()
        .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED
            || a.getStatus() == AppointmentStatus.CANCELLED)
        .sorted(
            Comparator.comparing((Appointment a) -> a.getAppointmentDate().toLocalDate()).reversed()
                .thenComparing(Appointment::getAppointmentDate)
        )
        .toList();
  }

  public void deleteAppointmentForUser(UUID appointmentId, UUID userId) {

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Такъв час не съществува."));

    if (!appointment.getUserId().equals(userId)) {
      throw new SecurityException("Нямате права да изтриете този час");
    }

    appointmentRepository.deleteById(appointmentId);
  }

  @Transactional
  public void editAppointmentForUser(UUID appointmentId,
      UUID userId,
      EditAppointmentRequest request) {

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Часът не е намерен."));

    if (!appointment.getUserId().equals(userId)) {
      throw new IllegalArgumentException("Нямате право да редактирате този час.");
    }

    LocalDateTime newDate = request.getAppointmentDate();
    int duration = appointment.getTreatment().getDurationMinutes();
    boolean isFree = isEmployeeAvailable(appointment.getEmployee(), newDate, duration);

    if (!isFree) {
      throw new NoFreeEmployeeException("Служителят е зает в този час.");
    }

    appointment.setAppointmentDate(newDate);
    appointmentRepository.save(appointment);
  }

  public EditAppointmentRequest prepareEditForm(UUID appointmentId, UUID userId) {

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Този час не съществува."));

    if (!appointment.getUserId().equals(userId)) {
      throw new IllegalArgumentException("Нямате права да редактирате този час.");
    }

    if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
        appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Този час не може да бъде редактиран.");
    }

    EditAppointmentRequest editAppointmentRequest = new EditAppointmentRequest();
    editAppointmentRequest.setAppointmentDate(appointment.getAppointmentDate());

    return editAppointmentRequest;
  }
}

