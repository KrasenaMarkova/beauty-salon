package com.example.beauty_salon.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.beauty_salon.restclient.UserServiceClient;
import com.example.beauty_salon.restclient.dto.StatusResponseDto;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.restclient.dto.UserRoleResponseDto;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.security.UserRole;
import com.example.beauty_salon.web.dto.EditProfileRequest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

  @Mock
  private UserServiceClient userServiceClient;

  @InjectMocks
  private UserService userService;

  @Test
  void whenUpdateProfileSuccessful_thenClientCalledOnce() {
    UUID userId = UUID.randomUUID();
    EditProfileRequest editRequest = EditProfileRequest.builder()
        .firstName("Test")
        .lastName("User")
        .phone("123456")
        .email("test@example.com")
        .build();

    UserDto updatedUser = UserDto.builder().id(userId).build();
    when(userServiceClient.updateUser(any(UserDto.class)))
        .thenReturn(ResponseEntity.ok(updatedUser));

    userService.updateProfile(userId, editRequest);

    verify(userServiceClient, times(1)).updateUser(any(UserDto.class));
  }

  @Test
  void whenUpdateProfileFails_thenThrowRuntimeException() {
    UUID userId = UUID.randomUUID();
    EditProfileRequest editRequest = EditProfileRequest.builder()
        .firstName("Test")
        .lastName("User")
        .phone("123456")
        .email("test@example.com")
        .build();

    when(userServiceClient.updateUser(any(UserDto.class)))
        .thenReturn(ResponseEntity.badRequest().build());

    assertThrows(RuntimeException.class, () -> userService.updateProfile(userId, editRequest));
  }

  @Test
  void whenLoadUserByUsername_thenReturnUserData() {
    String username = "testuser";
    UUID userId = UUID.randomUUID();

    UserDto mockUser = UserDto.builder()
        .id(userId)
        .username(username)
        .password("encodedPassword")
        .email("test@example.com")
        .userRole(UserRole.USER)
        .active(true)
        .build();

    when(userServiceClient.loadByUsername(username)).thenReturn(ResponseEntity.ok(mockUser));

    UserData userData = (UserData) userService.loadUserByUsername(username);

    assertEquals(userId, userData.getUserId());
    assertEquals(username, userData.getUsername());
    assertEquals("encodedPassword", userData.getPassword());
    assertEquals(UserRole.USER, userData.getRole());
    assertEquals("test@example.com", userData.getEmail());
    assertEquals(true, userData.isAccountActive());
  }

  @Test
  void whenLoadUserByUsernameReturnsNull_thenThrowNullPointerException() {
    String username = "notfound";

    when(userServiceClient.loadByUsername(username)).thenReturn(ResponseEntity.ok(null));

    assertThrows(NullPointerException.class, () -> {
      userService.loadUserByUsername(username);
    });
  }

  @Test
  void toggleStatus_shouldCallUserServiceClient() {
    UUID userId = UUID.randomUUID();
    StatusResponseDto mockResponse = StatusResponseDto.builder()
        .id(UUID.randomUUID())
        .active(true)
        .build();

    when(userServiceClient.toggleUserStatus(userId)).thenReturn(
        ResponseEntity.ok(mockResponse)
    );

    userService.toggleStatus(userId);

    verify(userServiceClient, times(1)).toggleUserStatus(userId);
  }

  @Test
  void toggleStatus_shouldThrowException_whenResponseIsNull() {
    UUID userId = UUID.randomUUID();

    when(userServiceClient.toggleUserStatus(userId)).thenReturn(
        ResponseEntity.ok(null)
    );

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      userService.toggleStatus(userId);
    });

    assertEquals("REST microservice returned null response", exception.getMessage());
  }

  @Test
  void toggleUserRole_shouldCallClientAndReturnSuccessfully() {
    UUID userId = UUID.randomUUID();

    UserRoleResponseDto mockResponse = UserRoleResponseDto.builder()
        .id(userId)
        .role(UserRole.USER) // използваме enum, а не String
        .build();

    when(userServiceClient.toggleUserRole(userId))
        .thenReturn(ResponseEntity.ok(mockResponse));

    userService.toggleUserRole(userId);

    verify(userServiceClient, times(1)).toggleUserRole(userId);
  }

  @Test
  void toggleUserRole_shouldThrowException_whenClientReturnsNull() {
    UUID userId = UUID.randomUUID();

    when(userServiceClient.toggleUserRole(userId))
        .thenReturn(ResponseEntity.ok(null));

    assertThrows(IllegalStateException.class, () -> userService.toggleUserRole(userId));

    verify(userServiceClient, times(1)).toggleUserRole(userId);
  }

  @Test
  void whenGetById_thenReturnUserDto() {
    UUID userId = UUID.randomUUID();

    UserDto mockUser = UserDto.builder()
        .id(userId)
        .username("testuser")
        .email("test@example.com")
        .build();

    when(userServiceClient.loadById(userId)).thenReturn(ResponseEntity.ok(mockUser));

    UserDto result = userService.getById(userId);

    assertEquals(mockUser, result);
  }

  @Test
  void whenGetAll_thenReturnListOfUsers() {
    UserDto user1 = UserDto.builder()
        .id(UUID.randomUUID())
        .username("user1")
        .email("user1@example.com")
        .build();

    UserDto user2 = UserDto.builder()
        .id(UUID.randomUUID())
        .username("user2")
        .email("user2@example.com")
        .build();

    List<UserDto> mockUsers = List.of(user1, user2);

    when(userServiceClient.listAllUsers()).thenReturn(ResponseEntity.ok(mockUsers));

    List<UserDto> result = userService.getAll();

    assertEquals(mockUsers, result);
  }
}
