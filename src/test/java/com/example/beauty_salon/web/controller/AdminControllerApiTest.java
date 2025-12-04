package com.example.beauty_salon.web.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.beauty_salon.beautytreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.employee.model.Employee;
import com.example.beauty_salon.employee.model.EmployeePosition;
import com.example.beauty_salon.employee.service.EmployeeService;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.security.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
public class AdminControllerApiTest {

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private EmployeeService employeeService;

  @MockitoBean
  private BeautyTreatmentService beautyTreatmentService;

  @Autowired
  private MockMvc mockMvc;

  private UserDetails adminUser() {
    return User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();
  }

  @Test
  void getAllUsers_asAdmin_shouldReturnUsersViewWithModel() throws Exception {

    List<UserDto> mockUsers = List.of(
        UserDto.builder()
            .id(UUID.randomUUID())
            .username("user1")
            .userRole(UserRole.USER)
            .active(true)
            .build(),
        UserDto.builder()
            .id(UUID.randomUUID())
            .username("user2")
            .userRole(UserRole.ADMIN)
            .active(true)
            .build()
    );

    when(userService.getAll()).thenReturn(mockUsers);

    mockMvc.perform(get("/admin/users")
            .with(user(adminUser())))
        .andExpect(status().isOk())
        .andExpect(view().name("users"))
        .andExpect(model().attribute("users", mockUsers));

    verify(userService).getAll();
  }

  @Test
  void postRequestToToggleUserStatus_asAdmin_shouldReturnRedirectAndInvokeServiceMethod() throws Exception {

    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID userId = UUID.randomUUID();

    mockMvc.perform(post("/admin/{id}/toggle-status", userId)
            .with(user(admin))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/users"))
        .andExpect(flash().attribute("message", "Статусът е променен успешно"));

    verify(userService).toggleStatus(userId);
  }

  @Test
  void postRequestToToggleUserRole_asAdmin_shouldReturnRedirectAndInvokeServiceMethod() throws Exception {

    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID userId = UUID.randomUUID();

    mockMvc.perform(post("/admin/{id}/toggle-role", userId)
            .with(user(admin))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/users"))
        .andExpect(flash().attribute("message", "Ролята на потребителя е променена успешно"));

    verify(userService).toggleUserRole(userId);
  }

  @Test
  void getAllEmployees_asAdmin_shouldReturnEmployeesViewWithModel() throws Exception {

    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    List<Employee> mockEmployees = List.of(
        Employee.builder()
            .id(UUID.randomUUID())
            .name("Ivan Ivanov")
            .employeePosition(EmployeePosition.HAIRDRESSER)
            .build(),
        Employee.builder()
            .id(UUID.randomUUID())
            .name("Maria Petrova")
            .employeePosition(EmployeePosition.COSMETICIAN)
            .build()
    );
    when(employeeService.getAll()).thenReturn(mockEmployees);

    mockMvc.perform(get("/admin/employees")
            .with(user(admin)))
        .andExpect(status().isOk())
        .andExpect(view().name("employees"))
        .andExpect(model().attribute("employees", mockEmployees));

    verify(employeeService).getAll();
  }

  @Test
  void getRegisterEmployeePage_asAdmin_shouldReturnAddEmployeeViewWithModel() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    mockMvc.perform(get("/admin/employees/add")
            .with(user(admin)))
        .andExpect(status().isOk())
        .andExpect(view().name("add-employee"))
        .andExpect(model().attributeExists("registerEmployeeRequest"))
        .andExpect(model().attribute("positions", EmployeePosition.values()));
  }

  @Test
  void postRegisterNewEmployee_asAdmin_shouldRedirectAndInvokeService() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    String name = "Ivan Ivanov";
    String position = "HAIRDRESSER";

    mockMvc.perform(post("/admin/employees/add")
            .param("name", name)
            .param("employeePosition", position)
            .with(user(admin))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/employees"))
        .andExpect(flash().attribute("successfulMessage", "Успешно добавихте служител."));

    verify(employeeService).register(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void postDeleteEmployee_asAdmin_shouldRedirectAndInvokeServiceMethod() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID employeeId = UUID.randomUUID();

    mockMvc.perform(post("/admin/employees/{id}/delete", employeeId)
            .with(user(admin))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/employees"));

    verify(employeeService).deleteById(employeeId);
  }

  @Test
  void getEditEmployeePage_asAdmin_shouldReturnEditEmployeeViewWithModel() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID employeeId = UUID.randomUUID();

    Employee mockEmployee = Employee.builder()
        .id(employeeId)
        .name("Ivan Ivanov")
        .employeePosition(EmployeePosition.HAIRDRESSER)
        .build();

    when(employeeService.getById(employeeId)).thenReturn(mockEmployee);

    mockMvc.perform(get("/admin/employees/{id}/edit", employeeId)
            .with(user(admin)))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-employee"))
        .andExpect(model().attribute("employee", mockEmployee))
        .andExpect(model().attribute("positions", EmployeePosition.values()));

    verify(employeeService).getById(employeeId);
  }

  @Test
  void postEditEmployee_asAdmin_shouldRedirectAndInvokeService() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID employeeId = UUID.randomUUID();

    mockMvc.perform(post("/admin/employees/{id}/edit", employeeId)
            .param("name", "Ivan Ivanov")
            .param("employeePosition", "HAIRDRESSER")
            .with(user(admin))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/employees"))
        .andExpect(flash().attribute("successfulMessage", "Промените са запазени успешно."));

    verify(employeeService).update(org.mockito.ArgumentMatchers.eq(employeeId),
        org.mockito.ArgumentMatchers.any());
  }

  @Test
  void getAllBeautyTreatment_asAdmin_shouldReturnTreatmentsViewWithModel() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    List<BeautyTreatment> mockTreatments = List.of(
        BeautyTreatment.builder()
            .id(UUID.randomUUID())
            .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
            .serviceDescription("Basic haircut service")
            .price(BigDecimal.valueOf(20.0))
            .durationMinutes(30)
            .build(),
        BeautyTreatment.builder()
            .id(UUID.randomUUID())
            .beautyTreatmentName(BeautyTreatmentName.MANICURE)
            .serviceDescription("Classic manicure")
            .price(BigDecimal.valueOf(25.0))
            .durationMinutes(45)
            .build()
    );

    when(beautyTreatmentService.getAll()).thenReturn(mockTreatments);

    mockMvc.perform(get("/admin/treatments")
            .with(user(admin)))
        .andExpect(status().isOk())
        .andExpect(view().name("treatments"))
        .andExpect(model().attribute("treatments", mockTreatments));

    verify(beautyTreatmentService).getAll();
  }

  @Test
  void getEditTreatmentsPage_asAdmin_shouldReturnEditTreatmentViewWithModel() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID treatmentId = UUID.randomUUID();

    BeautyTreatment mockTreatment = BeautyTreatment.builder()
        .id(treatmentId)
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .serviceDescription("Basic haircut")
        .price(BigDecimal.valueOf(20.0))
        .durationMinutes(30)
        .build();

    when(beautyTreatmentService.getById(treatmentId)).thenReturn(mockTreatment);

    mockMvc.perform(get("/admin/treatments/{id}/edit", treatmentId)
            .with(user(admin)))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-treatment"))
        .andExpect(model().attribute("editBeautyTreatmentRequest", mockTreatment))
        .andExpect(model().attribute("treatmentId", treatmentId));

    verify(beautyTreatmentService).getById(treatmentId);
  }

  @Test
  void postEditTreatment_asAdmin_shouldRedirectAndInvokeService() throws Exception {
    UserDetails admin = User.withUsername("admin")
        .password("password")
        .roles("ADMIN")
        .build();

    UUID treatmentId = UUID.randomUUID();

    mockMvc.perform(post("/admin/treatments/{id}/edit", treatmentId)
            .param("beautyTreatmentName", "HAIRCUT")
            .param("serviceDescription", "Updated haircut service")
            .param("price", "25.0")
            .param("durationMinutes", "35")
            .with(user(admin))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/treatments"))
        .andExpect(flash().attribute("updateSuccessful", "Промените са запазени успешно."));

    verify(beautyTreatmentService).update(org.mockito.ArgumentMatchers.eq(treatmentId),
        org.mockito.ArgumentMatchers.any());
  }
}
