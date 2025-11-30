package com.example.beauty_salon.employee.service;

import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import com.example.beauty_salon.web.dto.RegisterEmployeeRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }


  public List<Employee> getEmployeeByPosition(EmployeePosition requiredPosition) {
    return employeeRepository.findByEmployeePosition(requiredPosition);
  }

  public List<Employee> getAll() {
    return employeeRepository.findAll();
  }

  public void register(@Valid RegisterEmployeeRequest registerEmployeeRequest) {

    Employee employee = Employee.builder()
        .name(registerEmployeeRequest.getName())
        .employeePosition(registerEmployeeRequest.getEmployeePosition())
        .build();

    employeeRepository.save(employee);
  }

  public void deleteById(UUID id) {
    employeeRepository.deleteById(id);
  }

  public Employee getById(UUID id) {
    return employeeRepository.findById(id).orElse(null);
  }

  public void update(UUID id, @Valid RegisterEmployeeRequest editEmployeeRequest) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Служителят не съществува"));

    employee.setName(editEmployeeRequest.getName());
    employee.setEmployeePosition(editEmployeeRequest.getEmployeePosition());

    employeeRepository.save(employee);
  }
}
