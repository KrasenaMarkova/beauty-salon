package com.example.beauty_salon.restclient.dto;

import com.example.beauty_salon.security.UserRole;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRoleResponseDto {

  private UUID id;
  private UserRole role;

}
