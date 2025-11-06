package com.example.beauty_salon.user.service;

import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.model.UserRole;
import com.example.beauty_salon.user.repository.UserRepository;
import com.example.beauty_salon.web.dto.LoginRequest;
import com.example.beauty_salon.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            throw new RuntimeException("User with this email/username already exist.");
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
    }

    public User login(LoginRequest loginRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Incorrect username or password.");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password.");
        }

        return user;
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id [%s] does not exist.".formatted(userId)));
    }

    //@Cacheable("users")
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
}

