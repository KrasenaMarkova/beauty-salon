package com.example.beauty_salon.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceFeignConfig {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new UserServiceErrorDecoder();
  }
}
