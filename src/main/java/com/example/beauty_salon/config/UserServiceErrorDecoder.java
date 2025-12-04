package com.example.beauty_salon.config;

import com.example.beauty_salon.exception.ExternalServerException;
import com.example.beauty_salon.exception.UserNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.coyote.BadRequestException;

public class UserServiceErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultDecoder = new Default();

  @Override
  public Exception decode(String methodKey, Response response) {

    return switch (response.status()) {
      case 404 -> new UserNotFoundException("User not found");
      case 400 -> new BadRequestException("Bad request to user service");
      case 500 -> new ExternalServerException("User service internal error");
      default -> defaultDecoder.decode(methodKey, response);
    };
  }
}

