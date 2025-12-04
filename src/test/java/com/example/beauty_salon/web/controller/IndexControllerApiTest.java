package com.example.beauty_salon.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

import com.example.beauty_salon.appointment.model.Appointment;
import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.security.UserRole;
import com.example.beauty_salon.web.dto.RegisterRequest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private AppointmentService appointmentService;

  @Autowired
  private MockMvc mockMvc;

  @Captor
  private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

  @Test
  void getIndexEndpoint_shouldReturn200OkAndIndexView() throws Exception {

    MockHttpServletRequestBuilder httpRequest = get("/");

    mockMvc.perform(httpRequest)
        .andExpect(view().name("index"))
        .andExpect(status().isOk());
  }

  @Test
  void getRegisterPage_shouldReturn200AndRegisterView() throws Exception {
    mockMvc.perform(get("/register"))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("registerRequest"));
  }

  @Test
  void postRegister_shouldReturn302RedirectAndRedirectToLoginAndInvokeRegisterServiceMethod() throws Exception {

    MockHttpServletRequestBuilder httpRequest = post("/register")
        .with(csrf())
        .param("firstName", "Ivan")
        .param("lastName", "Ivanov")
        .param("username", "Ivanov")
        .param("email", "ivan@example.com")
        .param("phone", "0888123456")
        .param("password", "123456")
        .param("confirmPassword", "123456");

    mockMvc.perform(httpRequest)
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"))
        .andExpect(flash().attributeExists("registrationSuccessful"));

    verify(userService).register(registerRequestArgumentCaptor.capture());

    RegisterRequest dto = registerRequestArgumentCaptor.getValue();
    assertEquals("Ivan", dto.getFirstName());
    assertEquals("Ivanov", dto.getLastName());
    assertEquals("Ivanov", dto.getUsername());
    assertEquals("ivan@example.com", dto.getEmail());
    assertEquals("0888123456", dto.getPhone());
    assertEquals("123456", dto.getPassword());
    assertEquals("123456", dto.getConfirmPassword());
  }

  @Test
  void postRegisterWithInvalidFormData_shouldReturn200OkAndShowRegisterViewAndRegisterServiceMethodIsNeverInvoked() throws Exception {

    MockHttpServletRequestBuilder httpRequest = post("/register")
        .with(csrf())
        .param("firstName", "K")
        .param("lastName", "")
        .param("username", "V")
        .param("email", "invalid-email")
        .param("phone", "123")
        .param("password", "")
        .param("confirmPassword", "");

    mockMvc.perform(httpRequest)
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(
            model().attributeHasFieldErrors("registerRequest", "firstName", "lastName", "username", "email", "phone", "password", "confirmPassword"));

    verify(userService, never()).register(any());
  }

  @Test
  void registerNewUser_withInvalidData_shouldReturnRegisterView() throws Exception {
    mockMvc.perform(post("/register")
            .with(csrf())
            .param("firstName", "")
            .param("lastName", "Ivanov")
            .param("email", "ivan@example.com")
            .param("password", "Pass123"))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeHasFieldErrors("registerRequest", "firstName"));

    verify(userService, times(0)).register(any(RegisterRequest.class));
  }

  @Test
  void getHomePage_shouldReturnHomeView() throws Exception {

    UUID userId = UUID.randomUUID();
    UserData userData = new UserData(
        userId,
        "ivan",
        "pass",
        UserRole.USER,
        "a@b.com",
        true
    );

    UserDto dto = UserDto.builder()
        .id(userId)
        .username("krasi")
        .email("a@b.com")
        .build();

    when(userService.getById(userId)).thenReturn(dto);
    when(appointmentService.getAllSortedByUser(userId)).thenReturn(List.of());
    when(appointmentService.getActiveAppointments(userId)).thenReturn(List.of());

    mockMvc.perform(get("/home").with(user(userData)))
        .andExpect(status().isOk())
        .andExpect(view().name("home"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attributeExists("allAppointment"))
        .andExpect(model().attributeExists("activeAppointments"));
  }

  @Test
  void getLoginPage_shouldReturnLoginViewWithDefaultModel() throws Exception {

    mockMvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("login"))
        .andExpect(model().attributeExists("loginRequest"))
        .andExpect(model().attribute("loginAttemptMessage", (String) null))
        .andExpect(model().attributeDoesNotExist("errorMessage"));
  }
  @Test
  void getLoginPage_withLoginAttemptMessage_shouldShowItInModel() throws Exception {

    mockMvc.perform(get("/login")
            .param("loginAttemptMessage", "Опитайте отново"))
        .andExpect(status().isOk())
        .andExpect(view().name("login"))
        .andExpect(model().attributeExists("loginRequest"))
        .andExpect(model().attribute("loginAttemptMessage", "Опитайте отново"))
        .andExpect(model().attributeDoesNotExist("errorMessage"));
  }
}
