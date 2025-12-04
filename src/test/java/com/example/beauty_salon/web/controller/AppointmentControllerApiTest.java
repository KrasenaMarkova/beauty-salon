package com.example.beauty_salon.web.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.security.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AppointmentControllerApiTest {

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private AppointmentService appointmentService;

  @MockitoBean
  private BeautyTreatmentService beautyTreatmentService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser(username = "testuser")
  void whenCancelAppointment_thenRedirectWithSuccessMessage() throws Exception {
    UUID appointmentId = UUID.randomUUID();

    mockMvc.perform(post("/appointments/{id}/cancel", appointmentId)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(flash().attribute("successMessage", "Часът е успешно отменен."));

    verify(appointmentService, times(1)).cancelAppointment(appointmentId);
  }

  @Test
  void whenDeleteAppointment_thenRedirectWithSuccessMessage() throws Exception {
    UUID appointmentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    UserData userData = new UserData(
        userId,
        "testuser",
        "password",
        UserRole.USER,
        "test@example.com",
        true
    );

    doNothing().when(appointmentService).deleteAppointmentForUser(appointmentId, userId);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(
            userData,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        )
    );
    SecurityContextHolder.setContext(context);

    mockMvc.perform(post("/appointments/{id}/delete", appointmentId)
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/appointments/history"))
        .andExpect(flash().attribute("successMessage", "Часът беше изтрит успешно."));

    verify(appointmentService, times(1)).deleteAppointmentForUser(appointmentId, userId);
  }
}
