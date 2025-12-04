package com.example.beauty_salon.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerApiTest {

  @MockitoBean
  private AppointmentService appointmentService;

  @MockitoBean
  private BeautyTreatmentService beautyTreatmentService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser(username = "user", roles = {"USER"})
  void testShowBookingFormWithTreatmentsContent() throws Exception {
    BeautyTreatment facial = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .beautyTreatmentName(BeautyTreatmentName.FACIAL_CLEANSING)
        .serviceDescription("Refreshing facial")
        .price(new BigDecimal("50.00"))
        .durationMinutes(60)
        .build();

    when(beautyTreatmentService.getAll()).thenReturn(List.of(facial));

    mockMvc.perform(get("/booking"))
        .andExpect(status().isOk())
        .andExpect(view().name("booking"))
        .andExpect(model().attributeExists("treatments"))
        .andExpect(model().attributeExists("appointmentRequest"))
        .andExpect(model().attribute("treatments", List.of(facial)));
  }

  @Test
  @WithMockUser(username = "user", roles = {"USER"})
  void testShowBookingFormWithTreatments() throws Exception {
    BeautyTreatment treatment = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .beautyTreatmentName(BeautyTreatmentName.FACIAL_CLEANSING)
        .serviceDescription("Refreshing facial")
        .price(new BigDecimal("50.00"))
        .durationMinutes(60)
        .build();

    when(beautyTreatmentService.getAll()).thenReturn(List.of(treatment));

    mockMvc.perform(get("/booking"))
        .andExpect(status().isOk())
        .andExpect(view().name("booking"))
        .andExpect(model().attributeExists("treatments"))
        .andExpect(model().attributeExists("appointmentRequest"));
  }

  @Test
  @WithMockUser(username = "user", roles = {"USER"})
  void testBookAppointmentValidationError() throws Exception {
    BeautyTreatment facial = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .beautyTreatmentName(BeautyTreatmentName.FACIAL_CLEANSING)
        .serviceDescription("Refreshing facial")
        .price(new BigDecimal("50.00"))
        .durationMinutes(60)
        .build();

    when(beautyTreatmentService.getAll()).thenReturn(List.of(facial));

    mockMvc.perform(post("/booking")
            .param("appointmentDate", LocalDate.now().plusDays(1).toString()))
        .andExpect(status().isOk())
        .andExpect(view().name("booking"))
        .andExpect(model().attributeExists("error"))
        .andExpect(model().attributeExists("appointmentRequest"))
        .andExpect(model().attributeExists("treatments"));

    verify(appointmentService, never()).createAppointment(any(), any(), any());
  }
}

