package com.example.beauty_salon.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.beauty_salon.restclient.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

  @Test
  void fromUserToEditProfileRequest_whenUserWithDetailsIsPassed_thenDtoIsReturnedWithSameDetails() {

    UserDto user = UserDto.builder()
        .firstName("Ivan")
        .lastName("Ivanov")
        .email("ivan@test.bg")
        .phone("0884512369")
        .build();

    EditProfileRequest result = DtoMapper.fromUser(user);

    assertEquals("Ivan", result.getFirstName());
    assertEquals("Ivanov", result.getLastName());
    assertEquals("ivan@test.bg", result.getEmail());
    assertEquals("0884512369", result.getPhone());
  }
}