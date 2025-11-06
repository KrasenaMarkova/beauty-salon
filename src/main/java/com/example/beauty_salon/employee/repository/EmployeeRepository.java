package com.example.beauty_salon.employee.repository;

import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {


//     * Намира служител по неговата работна позиция (EmployeePosition).
//     * Използваме Optional, тъй като очакваме само ЕДИН или НУЛА служители с дадената позиция.
    Optional<Employee> findByEmployeePosition(EmployeePosition employeePosition);

    // Допълнителен метод, който може да е полезен за административна цел
    List<Employee> findAllByEmployeePosition(EmployeePosition employeePosition);
}

