package com.example.beauty_salon.email;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.beauty_salon.event.SuccessfulChargeEvent;
import org.junit.jupiter.api.Test;

public class EmailServiceUTest {

  private EmailService emailService = new EmailService();

  @Test
  void testSendEmail() {
    SuccessfulChargeEvent event = mock(SuccessfulChargeEvent.class);
    when(event.getEmail()).thenReturn("test@example.com");

    emailService.sendEmail(event);
  }
}
