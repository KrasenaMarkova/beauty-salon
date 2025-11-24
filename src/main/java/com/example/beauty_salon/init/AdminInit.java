package com.example.beauty_salon.init;

import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.model.UserRole;
import com.example.beauty_salon.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInit implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {

    if (userRepository.findByUsername("admin").isEmpty()) {

      User admin = new User();
      admin.setUsername("admin");
      admin.setFirstName("Admin");
      admin.setLastName("Adminov");
      admin.setEmail("admin@admin.bg");
      admin.setPhone("0000000000");
      admin.setPassword(passwordEncoder.encode("admin123"));
      admin.setUserRole(UserRole.ADMIN);
      admin.setActive(true);

      userRepository.save(admin);

      System.out.println("✅ ADMIN user created: username=admin, password=admin123");
    }
  }
}
