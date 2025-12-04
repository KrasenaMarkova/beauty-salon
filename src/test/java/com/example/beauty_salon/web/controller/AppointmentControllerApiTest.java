package com.example.beauty_salon.web.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.config.UserService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppointmentController.class)
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
}
