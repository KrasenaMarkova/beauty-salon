package com.example.beauty_salon.user.service;

import com.example.beauty_salon.event.SuccessfulChargeEvent;
import com.example.beauty_salon.exception.UserAlreadyExistsException;
import com.example.beauty_salon.exception.UserNotFoundException;
import com.example.beauty_salon.restclient.UserValidationClient;
import com.example.beauty_salon.restclient.dto.StatusResponseDto;
import com.example.beauty_salon.restclient.dto.UserRoleResponseDto;
import com.example.beauty_salon.restclient.dto.UserSyncDto;
import com.example.beauty_salon.restclient.dto.UserValidationRequestDto;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.model.UserRole;
import com.example.beauty_salon.user.repository.UserRepository;
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

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private final ApplicationEventPublisher eventPublisher;
  private final UserValidationClient userValidationClient;

  public void register(RegisterRequest registerRequest) {
    ResponseEntity<Boolean> userExists = userValidationClient.validateUserData(UserValidationRequestDto.builder()
            .username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
        .build());
    //

    if (!userExists.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("Communication error!");
    }

    if (Boolean.TRUE.equals( userExists.getBody())){
      throw new UserAlreadyExistsException("Потребителското име или email вече съществуват");
    }

    User user = User.builder()
        .firstName(registerRequest.getFirstName())
        .lastName(registerRequest.getLastName())
        .username(registerRequest.getUsername())
        .email(registerRequest.getEmail())
        .phone(registerRequest.getPhone())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .userRole(UserRole.USER)
        .active(true)
        .build();

    userRepository.save(user);

    ResponseEntity<String> response = userValidationClient.syncUser(
        UserSyncDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .phone(user.getPhone())
            .password(user.getPassword())
            .active(user.isActive())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .userRole(user.getUserRole().name())
            .build()
    );
    System.out.println(response.getBody());

    SuccessfulChargeEvent event = SuccessfulChargeEvent.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
    eventPublisher.publishEvent(event);

    log.info("New user profile was registered in the system for user [%s].".formatted(registerRequest.getUsername()));
  }

  public void updateProfile(UUID id, EditProfileRequest editProfileRequest) {

    User user = getById(id);

    user.setFirstName(editProfileRequest.getFirstName());
    user.setLastName(editProfileRequest.getLastName());
    user.setEmail(editProfileRequest.getEmail());
    user.setPhone(editProfileRequest.getPhone());

    userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Username not found"));

    return new UserData(user.getId(), username, user.getPassword(), user.getUserRole(), user.getEmail(), user.isActive());
  }

  public void toggleStatus(UUID id) {

    StatusResponseDto response = userValidationClient
        .toggleUserStatus(id)
        .getBody();

    if (response == null) {
      throw new IllegalStateException("REST microservice returned null response");
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Потребител с id [%s] не е намерен.".formatted(id)));

    user.setActive(response.isActive());
    userRepository.save(user);
  }

  public void toggleUserRole(UUID id) {
    UserRoleResponseDto response = userValidationClient
        .toggleUserRole(id)
        .getBody();

    if (response == null) {
      throw new IllegalStateException("REST microservice returned null response");
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setUserRole(response.getRole());
    userRepository.save(user);
  }

  public User getById(UUID userId) {

    return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Потребител с id [%s] не е намерен.".formatted(userId)));
  }

  public List<User> getAll() {

    return userRepository.findAll();
  }
}

