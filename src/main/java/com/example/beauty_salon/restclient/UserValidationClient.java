package com.example.beauty_salon.restclient;

import com.example.beauty_salon.restclient.dto.StatusResponseDto;
import com.example.beauty_salon.restclient.dto.UserSyncDto;
import com.example.beauty_salon.restclient.dto.UserValidationRequestDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "beauty-salon-rest",
    url = "http://localhost:8081/api/v1/users"  // адрес на микросървиса
)
public interface UserValidationClient {

  @PostMapping("/validation")
  ResponseEntity<Boolean> validateUserData(@RequestBody UserValidationRequestDto dto);

  @PostMapping("/sync")
  ResponseEntity<String> syncUser(@RequestBody UserSyncDto dto);

  @PutMapping("/toggle-status/{id}")
  ResponseEntity<StatusResponseDto> toggleUserStatus(@PathVariable UUID id);


}
