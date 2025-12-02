package com.example.beauty_salon.init;

import com.example.beauty_salon.restclient.UserServiceClient;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.restclient.dto.UserValidationRequestDto;
import com.example.beauty_salon.security.UserRole;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInit implements CommandLineRunner {

  private final UserServiceClient userServiceClient;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    try {
      Boolean exists = userServiceClient.validateUserData(
          UserValidationRequestDto.builder()
              .username("admin")
              .email("admin@admin.bg")
              .build()
      ).getBody();

      if (Boolean.FALSE.equals(exists)) {
        UserDto admin = new UserDto();
        admin.setUsername("admin");
        admin.setFirstName("Admin");
        admin.setLastName("Adminov");
        admin.setEmail("admin@admin.bg");
        admin.setPhone("0000000000");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setUserRole(UserRole.ADMIN);
        admin.setActive(true);

        userServiceClient.saveUser(admin);

        System.out.println("✅ ADMIN user created: username=admin, password=admin123");
      } else {
        System.out.println("ℹ️ Admin user already exists, skipping creation.");
      }

    } catch (FeignException e) {
      System.err.println("⚠️ Could not check or create admin: " + e.getMessage());
    }
  }
}

