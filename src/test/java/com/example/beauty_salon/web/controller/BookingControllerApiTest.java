package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IndexController.class)
public class BookingControllerApiTest {

  @MockitoBean
  private AppointmentService appointmentService;

  @MockitoBean
  private BeautyTreatmentService beautyTreatmentService;

  @Autowired
  private MockMvc mockMvc;


}

