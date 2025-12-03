package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class EditProfileRequest {

  @NotNull(message = "Not null firstName!")
  @Size(min = 3, max = 20, message = "Дължината на собственото име трябва да е между 3 и 20 знака.")
  private String firstName;

  @NotNull(message = "Not null lastName!")
  @Size(min = 3, max = 20, message = "Дължината на фамилията трябва да е между 3 и 20 знака.")
  private String lastName;

  @NotNull
  @Email
  private String email;

  @NotNull
  @Pattern(regexp = "[0-9]{10}", message = "Дължината на телефона трябва да е 10 цифри.")
  private String phone;
}
