package com.example.beauty_salon.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessfulChargeEvent {

  private UUID userId;

  private String username;

  private String email;

  private String firstName;

  private String lastName;
}
