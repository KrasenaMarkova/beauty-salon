package com.example.beauty_salon.restclient;

import com.example.beauty_salon.restclient.dto.UserSyncDto;
import com.example.beauty_salon.restclient.dto.UserValidationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
//    name = "validation-service",
    name = "beauty-salon-rest",
    url = "http://localhost:8081/api/v1/users"  // адрес на микросървиса
)
public interface UserValidationClient {

  @PostMapping("/validation")
  ResponseEntity<Boolean> validateUserData(@RequestBody UserValidationRequestDto dto);

  @PostMapping("/sync")
  void syncUser(@RequestBody UserSyncDto dto);
}
