package com.example.beauty_salon.web.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.beauty_salon.config.UserService;
import com.example.beauty_salon.restclient.dto.UserDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerApiTest {

  @MockitoBean
  private UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getProfilePage_shouldReturnProfileViewWithUserAndEditProfileRequest() throws Exception {
    UUID userId = UUID.randomUUID();

    UserDto userDto = UserDto.builder()
        .id(userId)
        .username("testuser")
        .firstName("Test")
        .lastName("User")
        .email("test@example.com")
        .build();

    when(userService.getById(userId)).thenReturn(userDto);

    mockMvc.perform(get("/users/{id}/profile", userId))
        .andExpect(status().isOk())
        .andExpect(view().name("profile-menu"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attributeExists("editProfileRequest"))
        .andExpect(model().attribute("user", userDto));

    verify(userService).getById(userId);
  }

  @Test
  void updateProfile_withValidationErrors_shouldReturnProfileMenuView() throws Exception {
    UUID userId = UUID.randomUUID();

    UserDto userDto = UserDto.builder()
        .id(userId)
        .username("testuser")
        .firstName("Test")
        .lastName("User")
        .email("test@example.com")
        .build();
    when(userService.getById(userId)).thenReturn(userDto);

    mockMvc.perform(put("/users/{id}/profile", userId)
            .param("firstName", "")
            .param("lastName", "User")
            .param("email", "invalid-email"))
        .andExpect(status().isOk())
        .andExpect(view().name("profile-menu"))
        .andExpect(model().attributeExists("user"));

    verify(userService).getById(userId);
  }
}
