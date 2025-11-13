package com.example.beauty_salon.bootstrap;

import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.repository.EmployeeRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

  private final BeautyTreatmentRepository beautyTreatmentRepository;
  private final EmployeeRepository employeeRepository;

  public DataLoader(BeautyTreatmentRepository beautyTreatmentRepository, EmployeeRepository employeeRepository) {
    this.beautyTreatmentRepository = beautyTreatmentRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public void run(String... args) throws Exception {

    if (beautyTreatmentRepository.count() == 0) {
      beautyTreatmentRepository.save(
          new BeautyTreatment(
              null, // id се генерира автоматично
              BeautyTreatmentName.HAIRCUT,
              "Класическа прическа с измиване и оформяне",
              new BigDecimal("30.00"),
              60
          )
      );

      beautyTreatmentRepository.save(
          new BeautyTreatment(
              null,
              BeautyTreatmentName.MANICURE,
              "Класически маникюр с лак",
              new BigDecimal("25.00"),
              60
          )
      );

      beautyTreatmentRepository.save(
          new BeautyTreatment(
              null,
              BeautyTreatmentName.FACIAL_CLEANSING,
              "Почистване на лице и хидратация",
              new BigDecimal("40.00"),
              60
          )
      );

      System.out.println("Начални услуги успешно заредени!");
    }

    // Зареждане на служители
    if (employeeRepository.count() == 0) {
      employeeRepository.save(Employee.builder()
          .name("Луна Локона")
          .employeePosition(EmployeePosition.HAIRDRESSER)
          .build());

      employeeRepository.save(Employee.builder()
          .name("Бъбъл Нейлс")
          .employeePosition(EmployeePosition.MANICURE)
          .build());

      employeeRepository.save(Employee.builder()
          .name("Скин Куийн")
          .employeePosition(EmployeePosition.COSMETICIAN)
          .build());

      System.out.println("Начални служители успешно заредени!");
    }
  }
}
