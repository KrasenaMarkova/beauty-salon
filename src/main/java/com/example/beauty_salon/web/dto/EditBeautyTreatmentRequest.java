package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditBeautyTreatmentRequest {

  @NotBlank
  private String serviceDescription;

}
