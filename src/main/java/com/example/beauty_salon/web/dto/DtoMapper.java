package com.example.beauty_salon.web.dto;

import com.example.beauty_salon.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static EditProfileRequest fromUser(User user) {

        return EditProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
