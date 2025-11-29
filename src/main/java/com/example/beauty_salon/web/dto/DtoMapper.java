package com.example.beauty_salon.web.dto;

import com.example.beauty_salon.restclient.dto.UserDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

  public static EditProfileRequest fromUser(UserDto user) {

    return EditProfileRequest.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .email(user.getEmail())
        .phone(user.getPhone())
        .build();
  }
}
