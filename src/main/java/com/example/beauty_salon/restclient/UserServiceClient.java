package com.example.beauty_salon.restclient;

import com.example.beauty_salon.restclient.dto.StatusResponseDto;
import com.example.beauty_salon.restclient.dto.UserRoleResponseDto;
import com.example.beauty_salon.restclient.dto.UserDto;
import com.example.beauty_salon.restclient.dto.UserValidationRequestDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "beauty-salon-rest", url = "http://localhost:8081/api/v1/users")
public interface UserServiceClient {

  @PostMapping("/validation")
  ResponseEntity<Boolean> validateUserData(@RequestBody UserValidationRequestDto dto);

  @PostMapping("/sync")
  ResponseEntity<String> syncUser(@RequestBody UserDto dto);

  @PutMapping("/toggle-status/{id}")
  ResponseEntity<StatusResponseDto> toggleUserStatus(@PathVariable UUID id);

  @PutMapping("/{id}/toggle-role")
  ResponseEntity<UserRoleResponseDto> toggleUserRole(@PathVariable UUID id);

  @PostMapping()
  ResponseEntity<UserDto> saveUser(@RequestBody UserDto dto);

  @PutMapping()
  ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto);

  @GetMapping()
  ResponseEntity<UserDto> loadByUsername(@RequestParam("username") String username);

  @GetMapping("/{id}")
  ResponseEntity<UserDto> loadById(@PathVariable("id") UUID id);

  @GetMapping("/list")
  ResponseEntity<List<UserDto>> listAllUsers();
}
