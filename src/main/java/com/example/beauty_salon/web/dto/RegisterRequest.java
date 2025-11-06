package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull(message = "Not null firstName!")
    @Size(min = 3, max = 20, message = "First name length must be between 3 and 20 characters!")
    private String firstName;

    @NotNull(message = "Not null lastName!")
    @Size(min = 3, max = 20, message = "Last name length must be between 3 and 20 characters!")
    private String lastName;

    @NotNull(message = "Not null username!")
    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters!")
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "[0-9]{10}", message = "phone length must be 10 characters!")
    private String phone;

    @NotNull
    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;

}
