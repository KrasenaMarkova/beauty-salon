package com.example.beauty_salon.user.service;

import com.example.beauty_salon.appointment.service.AppointmentService;
import com.example.beauty_salon.event.SuccessfulChargeEvent;
import com.example.beauty_salon.security.UserData;
import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.model.UserRole;
import com.example.beauty_salon.user.repository.UserRepository;
import com.example.beauty_salon.web.dto.EditProfileRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
  }

  public void register(RegisterRequest registerRequest) {

    Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());
    if (optionalUser.isPresent()) {
      throw new RuntimeException("User with [%s] username already exist.".formatted(registerRequest.getUsername()));
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
//    user.setPassword(passwordEncoder.encode(editProfileRequest.getPassword()));

    userRepository.save(user);
  }

  public User getById(UUID userId) {

    return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Потребител с id [%s] не е намерен.".formatted(userId)));
  }

  public List<User> getAll() {

    return userRepository.findAll();
  }

  public void deleteById(UUID id) {
    userRepository.deleteById(id);
  }

  public void toggleUserStatus(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Потребителят не е намерен!"));

    user.setActive(!user.isActive());
    userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username not found"));

    return new UserData(user.getId(), username, user.getPassword(), user.getUserRole(), user.getEmail(), user.isActive());
  }

  public void toggleUserRole(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Потребителят не е намерен"));

    if (user.getUserRole() == UserRole.ADMIN) {
      user.setUserRole(UserRole.USER);
    } else {
      user.setUserRole(UserRole.ADMIN);
    }

    userRepository.save(user);
  }

}

