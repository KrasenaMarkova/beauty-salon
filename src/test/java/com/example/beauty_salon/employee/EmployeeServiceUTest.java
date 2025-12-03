package com.example.beauty_salon.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.web.dto.RegisterEmployeeRequest;
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
public class EmployeeServiceUTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private EmployeeService employeeService;

  private UUID employeeId;

  @BeforeEach
  void setUp() {
    employeeId = UUID.randomUUID();
  }

  @Test
  void whenGetEmployeeByPosition_thenReturnEmployeesWithPosition() {
    Employee e1 = Employee.builder()
        .id(UUID.randomUUID())
        .name("Gosho")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    Employee e2 = Employee.builder()
        .id(UUID.randomUUID())
        .name("Pesho")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    when(employeeRepository.findByEmployeePosition(EmployeePosition.HAIRDRESSER))
        .thenReturn(List.of(e1, e2));

    List<Employee> result = employeeService.getEmployeeByPosition(EmployeePosition.HAIRDRESSER);

    assertEquals(2, result.size());
    assertTrue(result.contains(e1));
    assertTrue(result.contains(e2));
  }

  @Test
  void whenGetAll_thenReturnAllEmployees() {
    Employee e1 = Employee.builder()
        .id(UUID.randomUUID())
        .name("Gosho")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    Employee e2 = Employee.builder()
        .id(UUID.randomUUID())
        .name("Pesho")
        .employeePosition(EmployeePosition.MANICURE)
        .build();

    when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));

    List<Employee> result = employeeService.getAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(e1));
    assertTrue(result.contains(e2));
  }

  @Test
  void whenRegister_thenEmployeeSaved() {
    RegisterEmployeeRequest request = new RegisterEmployeeRequest();
    request.setName("Gosho");
    request.setEmployeePosition(EmployeePosition.HAIRDRESSER);

    employeeService.register(request);

    verify(employeeRepository).save(argThat(employee ->
        employee.getName().equals("Gosho") &&
            employee.getEmployeePosition() == EmployeePosition.HAIRDRESSER
    ));
  }

  @Test
  void whenDeleteById_thenRepositoryDeleteCalled() {
    employeeService.deleteById(employeeId);
    verify(employeeRepository).deleteById(employeeId);
  }

  @Test
  void whenGetById_andExists_thenReturnEmployee() {
    Employee employee = Employee.builder()
        .id(employeeId)
        .name("Gosho")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

    Employee result = employeeService.getById(employeeId);

    assertNotNull(result);
    assertEquals("Gosho", result.getName());
  }

  @Test
  void whenGetById_andDoesNotExist_thenReturnNull() {
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

    Employee result = employeeService.getById(employeeId);

    assertNull(result);
  }

  @Test
  void whenUpdate_thenModifyAndSaveEmployee() {
    Employee employee = Employee.builder()
        .id(employeeId)
        .name("Old Name")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    RegisterEmployeeRequest editRequest = new RegisterEmployeeRequest();
    editRequest.setName("New Name");
    editRequest.setEmployeePosition(EmployeePosition.MANICURE);

    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

    employeeService.update(employeeId, editRequest);

    assertEquals("New Name", employee.getName());
    assertEquals(EmployeePosition.MANICURE, employee.getEmployeePosition());
    verify(employeeRepository).save(employee);
  }

  @Test
  void whenUpdate_andEmployeeNotFound_thenThrowsException() {
    RegisterEmployeeRequest editRequest = new RegisterEmployeeRequest();
    editRequest.setName("New Name");
    editRequest.setEmployeePosition(EmployeePosition.MANICURE);

    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> employeeService.update(employeeId, editRequest));

    assertEquals("Служителят не съществува", ex.getMessage());
  }
}
