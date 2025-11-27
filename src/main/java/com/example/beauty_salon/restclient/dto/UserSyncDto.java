package com.example.beauty_salon.restclient.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSyncDto {

  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private String userRole;
  private String email;
  private String phone;
  private String password;
  private boolean active;
}
