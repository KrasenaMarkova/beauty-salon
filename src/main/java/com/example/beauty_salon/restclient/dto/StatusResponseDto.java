package com.example.beauty_salon.restclient.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusResponseDto {

  private UUID id;

  private boolean active;
}
