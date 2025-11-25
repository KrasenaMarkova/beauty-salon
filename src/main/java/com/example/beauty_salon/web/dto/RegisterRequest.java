package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

  @NotNull
  @Size(min = 3, max = 20, message = "Дължината на собственото име трябва да е между 3 и 20 знака!")
  private String firstName;

  @NotNull
  @Size(min = 3, max = 20, message = "Дължината на фамилията трябва да е между 3 и 20 знака!")
  private String lastName;

  @NotNull
  @Size(min = 3, max = 20, message = "Дължината на потребителското име трябва да е между 3 и 20 символа!")
  private String username;

  @NotNull
  @Email
  private String email;

  @NotNull
  @Pattern(regexp = "[0-9]{10}", message = "Дължината на телефона трябва да е 10 цифри!")
  private String phone;

  @NotNull
  @Size(min = 3, max = 20, message = "Дължината на паролата трябва да е между 3 и 20 знака!")
  private String password;

  @NotNull
  @Size(min = 3, max = 20, message = "Дължината на паролата трябва да е между 3 и 20 знака!")
  private String confirmPassword;

}
