package com.example.beauty_salon.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class UserDataUTest {

  @Test
  void testIsAccountFlags_WhenAccountIsActive_ShouldReturnTrue() {
    UserData user = new UserData(
        UUID.randomUUID(),
        "testuser",
        "password",
        UserRole.USER,
        "test@mail.com",
        true
    );

    assertThat(user.isAccountNonExpired()).isTrue();
    assertThat(user.isAccountNonLocked()).isTrue();
    assertThat(user.isCredentialsNonExpired()).isTrue();
    assertThat(user.isEnabled()).isTrue();
  }

  @Test
  void testIsAccountFlags_WhenAccountIsInactive_ShouldReturnFalse() {
    UserData user = new UserData(
        UUID.randomUUID(),
        "testuser",
        "password",
        UserRole.USER,
        "test@mail.com",
        false
    );

    assertThat(user.isAccountNonExpired()).isFalse();
    assertThat(user.isAccountNonLocked()).isFalse();
    assertThat(user.isCredentialsNonExpired()).isFalse();
    assertThat(user.isEnabled()).isFalse();
  }

  @Test
  void testBasicGetters() {
    UUID id = UUID.randomUUID();

    UserData user = new UserData(
        id,
        "username",
        "secret",
        UserRole.USER,
        "mail@mail.com",
        true
    );

    assertThat(user.getUserId()).isEqualTo(id);
    assertThat(user.getUsername()).isEqualTo("username");
    assertThat(user.getPassword()).isEqualTo("secret");
  }
}
