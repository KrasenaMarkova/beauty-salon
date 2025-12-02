package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.web.dto.EditBeautyTreatmentRequest;
import com.example.beauty_salon.web.dto.RegisterEmployeeRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;
  private final EmployeeService employeeService;
  private final BeautyTreatmentService beautyTreatmentService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  public ModelAndView getAllUsers() {

    List<UserDto> users = userService.getAll();

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("users");
    modelAndView.addObject("users", users);

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/toggle-status")
  public String toggleUserStatus(@PathVariable UUID id, RedirectAttributes redirectAttributes) {

    userService.toggleStatus(id);

    redirectAttributes.addFlashAttribute("message", "Статусът е променен успешно");

    return "redirect:/admin/users";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/toggle-role")
  public String toggleUserRole(@PathVariable UUID id, RedirectAttributes redirectAttributes) {

    userService.toggleUserRole(id);

    redirectAttributes.addFlashAttribute("message", "Ролята на потребителя е променена успешно");

    return "redirect:/admin/users";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/employees")
  public ModelAndView getAllEmployees() {

    List<Employee> employees = employeeService.getAll();

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("employees");
    modelAndView.addObject("employees", employees);

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/employees/add")
  public ModelAndView getRegisterEmployeePage() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("add-employee");
    modelAndView.addObject("registerEmployeeRequest", new RegisterEmployeeRequest());
    modelAndView.addObject("positions", EmployeePosition.values());

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/employees/add")
  public ModelAndView registerNewEmployee(@Valid RegisterEmployeeRequest registerEmployeeRequest, RedirectAttributes redirectAttributes) {

    employeeService.register(registerEmployeeRequest);

    redirectAttributes.addFlashAttribute("successfulMessage", "Успешно добавихте служител.");

    return new ModelAndView("redirect:/admin/employees");
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/employees/{id}/delete")
  public String deleteEmployee(@PathVariable UUID id) {

    employeeService.deleteById(id);

    return "redirect:/admin/employees";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/employees/{id}/edit")
  public ModelAndView getEditEmployeePage(@PathVariable UUID id) {

    Employee employee = employeeService.getById(id);

    ModelAndView modelAndView = new ModelAndView("edit-employee");
    modelAndView.addObject("employee", employee);
    modelAndView.addObject("positions", EmployeePosition.values());

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/employees/{id}/edit")
  public ModelAndView editEmployee(@PathVariable UUID id, @Valid RegisterEmployeeRequest editEmployeeRequest, RedirectAttributes redirectAttributes) {

    employeeService.update(id, editEmployeeRequest);

    redirectAttributes.addFlashAttribute("successfulMessage", "Промените са запазени успешно.");

    return new ModelAndView("redirect:/admin/employees");
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/treatments")
  public ModelAndView getAllBeautyTreatment() {

    List<BeautyTreatment> treatments = beautyTreatmentService.getAll();

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("treatments");
    modelAndView.addObject("treatments", treatments);

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/treatments/{id}/edit")
  public ModelAndView getEditTreatmentsPage(@PathVariable UUID id) {

    BeautyTreatment beautyTreatment = beautyTreatmentService.getById(id);

    ModelAndView modelAndView = new ModelAndView("edit-treatment");
    modelAndView.addObject("editBeautyTreatmentRequest", beautyTreatment);
    modelAndView.addObject("treatmentId", beautyTreatment.getId());

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/treatments/{id}/edit")
  public ModelAndView editTreatmentsPage(
      @PathVariable UUID id,
      @Valid EditBeautyTreatmentRequest editBeautyTreatmentRequest,
      RedirectAttributes redirectAttributes) {

    beautyTreatmentService.update(id, editBeautyTreatmentRequest);
    redirectAttributes.addFlashAttribute("updateSuccessful", "Промените са запазени успешно.");

    return new ModelAndView("redirect:/admin/treatments");
  }
}
