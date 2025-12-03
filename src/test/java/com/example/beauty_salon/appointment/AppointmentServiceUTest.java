package com.example.beauty_salon.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.model.AppointmentStatus;
import com.example.beauty_salon.appointment.repository.AppointmentRepository;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.web.dto.EditAppointmentRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceUTest {

  @Mock
  private AppointmentRepository appointmentRepository;

  @Mock
  private EmployeeService employeeService;

  @Mock
  private UserService userService;

  @Mock
  private BeautyTreatmentService beautyTreatmentService;

  @InjectMocks
  private AppointmentService appointmentService;

  private UUID userId;
  private UUID appointmentId;
  private UUID appointmentId1;
  private UUID appointmentId2;
  private UUID treatmentId;
  private LocalDateTime appointmentTime1;
  private LocalDateTime appointmentTime2;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    appointmentId = UUID.randomUUID();
    appointmentId1 = UUID.randomUUID();
    appointmentId2 = UUID.randomUUID();
    treatmentId = UUID.randomUUID();
    appointmentTime1 = LocalDateTime.of(2025, 12, 4, 10, 0);
    appointmentTime2 = LocalDateTime.of(2025, 12, 5, 12, 0);
  }

  @Test
  void whenCreateAppointmentWithAvailableEmployee_thenSavesAppointment() {
    UserDto userDto = UserDto.builder().id(userId).build();
    BeautyTreatment treatment = BeautyTreatment.builder()
        .id(treatmentId)
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .durationMinutes(60)
        .price(BigDecimal.valueOf(50.0))
        .build();
    Employee employee = Employee.builder()
        .id(UUID.randomUUID())
        .name("Pesho")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    when(userService.getById(userId)).thenReturn(userDto);
    when(beautyTreatmentService.getById(treatmentId)).thenReturn(treatment);
    when(employeeService.getEmployeeByPosition(EmployeePosition.HAIRDRESSER))
        .thenReturn(List.of(employee));
    when(appointmentRepository.findAllByEmployeeId(employee.getId())).thenReturn(List.of());

    appointmentService.createAppointment(userId, treatmentId, appointmentTime1);

    verify(appointmentRepository).save(any(Appointment.class));
  }

  @Test
  void whenNoEmployeesForTreatment_thenThrowsException() {
    UserDto userDto = UserDto.builder().id(userId).build();
    BeautyTreatment treatment = BeautyTreatment.builder()
        .id(treatmentId)
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .durationMinutes(60)
        .price(BigDecimal.valueOf(50.0))
        .build();

    when(userService.getById(userId)).thenReturn(userDto);
    when(beautyTreatmentService.getById(treatmentId)).thenReturn(treatment);
    when(employeeService.getEmployeeByPosition(EmployeePosition.HAIRDRESSER)).thenReturn(List.of());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> appointmentService.createAppointment(userId, treatmentId, appointmentTime1));

    assertEquals("Няма наличен служител за тази услуга.", ex.getMessage());
  }

  @Test
  void whenNoAvailableEmployeeForTime_thenThrowsException() {
    UserDto userDto = UserDto.builder().id(userId).build();
    BeautyTreatment treatment = BeautyTreatment.builder()
        .id(treatmentId)
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .durationMinutes(60)
        .price(BigDecimal.valueOf(50.0))
        .build();

    Employee employee = Employee.builder()
        .id(UUID.randomUUID())
        .name("Pesho")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    Appointment existingAppointment = Appointment.builder()
        .appointmentDate(appointmentTime1)
        .durationMinutes(60)
        .price(BigDecimal.valueOf(50.0))
        .status(AppointmentStatus.SCHEDULED)
        .employee(employee)
        .build();

    when(userService.getById(userId)).thenReturn(userDto);
    when(beautyTreatmentService.getById(treatmentId)).thenReturn(treatment);
    when(employeeService.getEmployeeByPosition(EmployeePosition.HAIRDRESSER))
        .thenReturn(List.of(employee));
    when(appointmentRepository.findAllByEmployeeId(employee.getId()))
        .thenReturn(List.of(existingAppointment));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> appointmentService.createAppointment(userId, treatmentId, appointmentTime1));

    assertEquals("Няма свободен служител за избрания час.", ex.getMessage());
  }

  @Test
  void whenPastAppointmentsExist_thenMarkAsCompleted() {
    Appointment pastAppointment = Appointment.builder()
        .id(appointmentId)
        .appointmentDate(appointmentTime1.minusDays(1))
        .durationMinutes(60)
        .status(AppointmentStatus.SCHEDULED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Gosho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    when(appointmentRepository.findAll()).thenReturn(List.of(pastAppointment));

    appointmentService.markPastAppointmentsAsCompleted();

    assertEquals(AppointmentStatus.COMPLETED, pastAppointment.getStatus());
    verify(appointmentRepository).saveAll(List.of(pastAppointment));
  }

  @Test
  void whenGetAllByUserId_thenReturnAppointments() {
    Appointment a1 = Appointment.builder().id(appointmentId).userId(userId).build();
    when(appointmentRepository.findByUserId(userId)).thenReturn(List.of(a1));

    List<Appointment> result = appointmentService.getAllByUserId(userId);

    assertEquals(1, result.size());
    assertEquals(appointmentId, result.get(0).getId());
  }

  @Test
  void whenGetById_thenReturnAppointment() {
    Appointment a = Appointment.builder().id(appointmentId).build();
    when(appointmentRepository.getById(appointmentId)).thenReturn(a);

    Appointment result = appointmentService.getById(appointmentId);

    assertEquals(appointmentId, result.getId());
  }

  @Test
  void whenCancelScheduledAppointment_thenStatusUpdated() {
    Appointment a = Appointment.builder()
        .id(appointmentId)
        .status(AppointmentStatus.SCHEDULED)
        .build();
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(a));

    appointmentService.cancelAppointment(appointmentId);

    assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
    verify(appointmentRepository).save(a);
  }

  @Test
  void whenCancelAlreadyCancelledAppointment_thenThrows() {
    Appointment a = Appointment.builder()
        .id(appointmentId)
        .status(AppointmentStatus.CANCELLED)
        .build();
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(a));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> appointmentService.cancelAppointment(appointmentId));

    assertEquals("Този час вече е отменен.", ex.getMessage());
  }

  // --- deleteAppointmentForUser ---
  @Test
  void whenDeleteAppointmentForUserWithCorrectUserId_thenDelete() {
    Appointment a = Appointment.builder().id(appointmentId).userId(userId).build();
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(a));

    appointmentService.deleteAppointmentForUser(appointmentId, userId);

    verify(appointmentRepository).deleteById(appointmentId);
  }

  @Test
  void whenDeleteAppointmentForUserWithWrongUserId_thenThrows() {
    Appointment a = Appointment.builder().id(appointmentId).userId(UUID.randomUUID()).build();
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(a));

    SecurityException ex = assertThrows(SecurityException.class,
        () -> appointmentService.deleteAppointmentForUser(appointmentId, userId));

    assertEquals("Нямате права да изтриете този час", ex.getMessage());
  }

  @Test
  void whenPrepareEditFormWithValidAppointment_thenReturnEditRequest() {
    Appointment a = Appointment.builder()
        .id(appointmentId)
        .userId(userId)
        .status(AppointmentStatus.SCHEDULED)
        .appointmentDate(appointmentTime1.plusDays(1))
        .build();
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(a));

    EditAppointmentRequest request = appointmentService.prepareEditForm(appointmentId, userId);

    assertEquals(a.getAppointmentDate(), request.getAppointmentDate());
  }

  @Test
  void whenPrepareEditFormForCancelledAppointment_thenThrows() {
    Appointment a = Appointment.builder()
        .id(appointmentId)
        .userId(userId)
        .status(AppointmentStatus.CANCELLED)
        .appointmentDate(appointmentTime1.plusDays(1))
        .build();
    when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(a));

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> appointmentService.prepareEditForm(appointmentId, userId));

    assertEquals("Този час не може да бъде редактиран.", ex.getMessage());
  }

  @Test
  void whenGetAllSortedByUser_thenReturnSortedList() {
    Appointment a1 = Appointment.builder()
        .id(appointmentId2)
        .userId(userId)
        .appointmentDate(appointmentTime2)
        .status(AppointmentStatus.SCHEDULED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Gosho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    Appointment a2 = Appointment.builder()
        .id(appointmentId1)
        .userId(userId)
        .appointmentDate(appointmentTime1)
        .status(AppointmentStatus.SCHEDULED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Pesho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    when(appointmentRepository.findAllByUserId(userId)).thenReturn(List.of(a1, a2));

    List<Appointment> result = appointmentService.getAllSortedByUser(userId);

    assertEquals(2, result.size());
    assertEquals(appointmentId1, result.get(0).getId());  // проверява правилната сортирана поредност
    assertEquals(appointmentId2, result.get(1).getId());
  }

  @Test
  void whenGetActiveAppointments_thenReturnOnlyScheduledSorted() {
    Appointment scheduled = Appointment.builder()
        .id(appointmentId1)
        .userId(userId)
        .appointmentDate(appointmentTime1)
        .status(AppointmentStatus.SCHEDULED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Gosho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    Appointment cancelled = Appointment.builder()
        .id(appointmentId2)
        .userId(userId)
        .appointmentDate(appointmentTime2)
        .status(AppointmentStatus.CANCELLED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Pesho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    when(appointmentRepository.findAllByUserId(userId)).thenReturn(List.of(cancelled, scheduled));

    List<Appointment> result = appointmentService.getActiveAppointments(userId);

    assertEquals(1, result.size());
    assertEquals(AppointmentStatus.SCHEDULED, result.get(0).getStatus());
    assertEquals(appointmentId1, result.get(0).getId());
  }

  @Test
  void whenGetPastAppointmentsForUser_thenReturnCompletedOrCancelledSorted() {
    Appointment completed = Appointment.builder()
        .id(appointmentId1)
        .userId(userId)
        .appointmentDate(appointmentTime1)
        .status(AppointmentStatus.COMPLETED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Gosho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    Appointment cancelled = Appointment.builder()
        .id(appointmentId2)
        .userId(userId)
        .appointmentDate(appointmentTime2)
        .status(AppointmentStatus.CANCELLED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Pesho").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    Appointment scheduled = Appointment.builder()
        .id(UUID.randomUUID())
        .userId(userId)
        .appointmentDate(appointmentTime2.plusDays(1))
        .status(AppointmentStatus.SCHEDULED)
        .employee(Employee.builder().id(UUID.randomUUID()).name("Ivan").employeePosition(EmployeePosition.HAIRDRESSER).build())
        .price(BigDecimal.valueOf(50))
        .build();

    when(appointmentRepository.findByUserId(userId)).thenReturn(List.of(scheduled, cancelled, completed));

    List<Appointment> result = appointmentService.getPastAppointmentsForUser(userId);

    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(a -> a.getStatus() == AppointmentStatus.COMPLETED || a.getStatus() == AppointmentStatus.CANCELLED));
    assertEquals(appointmentId2, result.get(0).getId()); // първо cancelled (по дата desc)
    assertEquals(appointmentId1, result.get(1).getId()); // после completed
  }
}
