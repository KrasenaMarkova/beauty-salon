package com.example.beauty_salon.email;

import com.example.beauty_salon.event.SuccessfulChargeEvent;
import java.time.LocalDateTime;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Async
  @EventListener
  public void sendEmail(SuccessfulChargeEvent event) {

    System.out.println("Sending email for registration for user with email: " + event.getEmail());
  }

  @Async
  public void sendAppointmentReminder(String email, String treatmentName, LocalDateTime appointmentDate) {
    System.out.println("Sending reminder to: " + email +
        " for treatment: " + treatmentName +
        " at: " + appointmentDate);
  }

}
