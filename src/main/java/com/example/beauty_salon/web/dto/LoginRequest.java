package com.example.beauty_salon.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull(message = "Not null username!")
    @Size(min = 3, max = 15, message = "Username length must be between 3 and 15 characters!")
    private String username;

    @NotNull(message = "Not null password!")
    @Size(min = 3, max = 15, message = "Password length must be between 3 and 15 characters!")
    private String password;
}
