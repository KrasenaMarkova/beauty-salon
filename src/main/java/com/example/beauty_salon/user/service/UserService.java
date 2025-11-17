package com.example.beauty_salon.user.service;

import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.model.UserRole;
import com.example.beauty_salon.user.repository.UserRepository;
import com.example.beauty_salon.web.dto.EditProfileRequest;
import com.example.beauty_salon.web.dto.LoginRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new RuntimeException("User with [%s] username already exist.".formatted(registerRequest.getUsername()));
        }

//        User excistsUser = userRepository
//                .findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail())
//               .orElseThrow(() -> new UserAlreadyExists("User with [%s] username already exist.".formatted(registerRequest.getUsername())));
        /*if (optionalUser.isPresent()) {
            throw new RuntimeException("User with [%s] username already exist.".formatted(registerRequest.getUsername()));
        }*/

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

        log.info("New user profile was registered in the system for user [%s].".formatted(registerRequest.getUsername()));
    }

    public User login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with [%s] username does not exist.".formatted(loginRequest.getUsername()));
        }

        String rawPassword = loginRequest.getPassword();
        String hashedPassword = optionalUser.get().getPassword();
        if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
            throw new RuntimeException("Incorrect username or password.");
        }

        return optionalUser.get();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void updateProfile(UUID id, EditProfileRequest editProfileRequest) {

        User user = getById(id);

        user.setFirstName(editProfileRequest.getFirstName());
        user.setLastName(editProfileRequest.getLastName());
        user.setEmail(editProfileRequest.getEmail());
        user.setPhone(editProfileRequest.getPhone());
        user.setPassword(passwordEncoder.encode(editProfileRequest.getPassword()));

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

//    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {

        User user = getById(userId);
        user.setActive(!user.isActive());

        userRepository.save(user);
    }

}

