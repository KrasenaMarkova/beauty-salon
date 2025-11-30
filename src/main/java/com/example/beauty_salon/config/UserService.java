package com.example.beauty_salon.config;

import com.example.beauty_salon.event.SuccessfulChargeEvent;
import com.example.beauty_salon.exception.UserAlreadyExistsException;
import com.example.beauty_salon.restclient.UserServiceClient;
import com.example.beauty_salon.restclient.dto.StatusResponseDto;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.restclient.dto.UserRoleResponseDto;
import com.example.beauty_salon.restclient.dto.UserValidationRequestDto;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.security.UserRole;
import com.example.beauty_salon.web.dto.EditProfileRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final ApplicationEventPublisher eventPublisher;
  private final UserServiceClient userServiceClient;
  private final PasswordEncoder passwordEncoder;

  public void register(RegisterRequest registerRequest) {
    ResponseEntity<Boolean> userExists = userServiceClient.validateUserData(UserValidationRequestDto.builder()
        .username(registerRequest.getUsername())
        .email(registerRequest.getEmail())
        .build());

    if (!userExists.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("Communication error!");
    }

    if (Boolean.TRUE.equals(userExists.getBody())) {
      throw new UserAlreadyExistsException("Потребителското име или email вече съществуват");
    }

    UserDto userDto = UserDto.builder()
        .firstName(registerRequest.getFirstName())
        .lastName(registerRequest.getLastName())
        .username(registerRequest.getUsername())
        .email(registerRequest.getEmail())
        .phone(registerRequest.getPhone())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .userRole(UserRole.USER)
        .build();

    ResponseEntity<UserDto> userResponse = userServiceClient.saveUser(userDto);

    if (!userResponse.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("User creation error!");
    }

    log.info("New user profile was registered in the system for user [%s].".formatted(registerRequest.getUsername()));
    UserDto user = userResponse.getBody();

    SuccessfulChargeEvent event = SuccessfulChargeEvent.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
    eventPublisher.publishEvent(event);
  }

  public void updateProfile(UUID id, EditProfileRequest editProfileRequest) {

    UserDto userDto = UserDto.builder()
        .firstName(editProfileRequest.getFirstName())
        .lastName(editProfileRequest.getLastName())
        .phone(editProfileRequest.getPhone())
        .email(editProfileRequest.getEmail())
        .build();
    System.out.println(editProfileRequest.getPhone());

    ResponseEntity<UserDto> responseEntity = userServiceClient.updateUser(userDto);
    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("Something went wrong!");
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserDto user = userServiceClient.loadByUsername(username).getBody();

    return new UserData(user.getId(), username, user.getPassword(), user.getUserRole(), user.getEmail(), user.isActive());
  }

  public void toggleStatus(UUID id) {

    StatusResponseDto response = userServiceClient
        .toggleUserStatus(id)
        .getBody();

    if (response == null) {
      throw new IllegalStateException("REST microservice returned null response");
    }
  }

  public void toggleUserRole(UUID id) {
    UserRoleResponseDto response = userServiceClient
        .toggleUserRole(id)
        .getBody();

    if (response == null) {
      throw new IllegalStateException("REST microservice returned null response");
    }
  }

  public UserDto getById(UUID userId) {

    return userServiceClient
        .loadById(userId)
        .getBody();
  }

  public List<UserDto> getAll() {

    return userServiceClient
        .listAllUsers()
        .getBody();
  }

}

