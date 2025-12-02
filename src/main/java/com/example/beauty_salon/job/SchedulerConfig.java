package com.example.beauty_salon.job;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.beautyTreatment.service.BeautyTreatmentService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SchedulerConfig {

  private final BeautyTreatmentService beautyTreatmentService;
  private final AppointmentService appointmentService;

  public SchedulerConfig(BeautyTreatmentService beautyTreatmentService,
      AppointmentService appointmentService) {
    this.beautyTreatmentService = beautyTreatmentService;
    this.appointmentService = appointmentService;
  }

  @Scheduled(cron = "0 0 3 1 * *")
  public void recalculateMonthlyTreatmentPrices() {
    beautyTreatmentService.adjustPricesForInflation();
    System.out.println("Monthly treatment prices updated at " + java.time.LocalDateTime.now());
  }

  @Scheduled(fixedRate = 5 * 60 * 1000)
  public void updatePastAppointmentsStatus() {
    appointmentService.markPastAppointmentsAsCompleted();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    String formattedNow = LocalDateTime.now().format(formatter);

    System.out.println("Updated past appointments status at " + formattedNow);
  }
}