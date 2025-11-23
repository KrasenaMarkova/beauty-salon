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
  @Size(min = 3, max = 20, message = "First name length must be between 3 and 20 characters!")
  private String firstName;

  @NotNull(message = "Not null lastName!")
  @Size(min = 3, max = 20, message = "Last name length must be between 3 and 20 characters!")
  private String lastName;

  @NotNull
  @Email
  private String email;

  @NotNull
  @Pattern(regexp = "[0-9]{10}", message = "phone length must be 10 digits!")
  private String phone;

  @NotNull
  @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
  private String password;

}
