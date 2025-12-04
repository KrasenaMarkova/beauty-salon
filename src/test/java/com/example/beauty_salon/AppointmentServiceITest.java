package com.example.beauty_salon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.repository.AppointmentRepository;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautytreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.exception.NoFreeEmployeeException;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AppointmentServiceITest {

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private BeautyTreatmentRepository beautyTreatmentRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private AppointmentService appointmentService;

  private UUID userId;
  private UUID appointmentId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    Employee employee = employeeRepository.findAll().get(0);
    BeautyTreatment treatment = beautyTreatmentRepository.findAll().get(0);

    Appointment appointment = new Appointment();
    appointment.setUserId(userId);
    appointment.setEmployee(employee);
    appointment.setTreatment(treatment);
    appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
    appointment.setPrice(treatment.getPrice());
    appointment.setDurationMinutes(treatment.getDurationMinutes());
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    appointmentRepository.save(appointment);

    appointmentId = appointment.getId();
  }

  @Test
  void editAppointment_WrongUser_ThrowsException() {
    EditAppointmentRequest request = new EditAppointmentRequest();
    request.setAppointmentDate(LocalDateTime.now().plusDays(2));

    UUID wrongUserId = UUID.randomUUID();

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> appointmentService.editAppointmentForUser(appointmentId, wrongUserId, request));
    assertEquals("Нямате право да редактирате този час.", ex.getMessage());
  }

  @Test
  void editAppointment_EmployeeBusy_ThrowsException() {

    Appointment busyAppointment = new Appointment();
    busyAppointment.setUserId(UUID.randomUUID());
    busyAppointment.setEmployee(employeeRepository.findAll().get(0));
    busyAppointment.setTreatment(beautyTreatmentRepository.findAll().get(0));
    busyAppointment.setAppointmentDate(LocalDateTime.now().plusDays(2));
    busyAppointment.setPrice(busyAppointment.getTreatment().getPrice());
    busyAppointment.setDurationMinutes(busyAppointment.getTreatment().getDurationMinutes());
    busyAppointment.setStatus(AppointmentStatus.SCHEDULED);
    appointmentRepository.save(busyAppointment);

    EditAppointmentRequest request = new EditAppointmentRequest();
    request.setAppointmentDate(busyAppointment.getAppointmentDate());

    assertThrows(NoFreeEmployeeException.class,
        () -> appointmentService.editAppointmentForUser(appointmentId, userId, request));
  }
}
