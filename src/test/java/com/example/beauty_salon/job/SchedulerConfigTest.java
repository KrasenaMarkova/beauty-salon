package com.example.beauty_salon.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchedulerConfigTest {

  @Mock
  private BeautyTreatmentService beautyTreatmentService;

  @Mock
  private AppointmentService appointmentService;

  @InjectMocks
  private SchedulerConfig schedulerConfig;

  @Test
  void recalculateMonthlyTreatmentPrices_shouldCallAdjustPricesForInflation() {

    schedulerConfig.recalculateMonthlyTreatmentPrices();

    verify(beautyTreatmentService, times(1)).adjustPricesForInflation();
  }

  @Test
  void updatePastAppointmentsStatus_shouldCallMarkPastAppointmentsAsCompleted() {

    schedulerConfig.updatePastAppointmentsStatus();

    verify(appointmentService, times(1)).markPastAppointmentsAsCompleted();
  }
}