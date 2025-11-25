package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class LoginRequest {

  @NotNull(message = "Not null username!")
  @Size(min = 3, max = 15, message = "Дължината на потребителското име трябва да е между 3 и 20 символа!")
  private String username;

  @NotNull(message = "Not null password!")
  @Size(min = 3, max = 15, message = "Дължината на паролата трябва да е между 3 и 20 знака!")
  private String password;
}
