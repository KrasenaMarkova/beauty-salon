package com.example.beauty_salon.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.beauty_salon.exception.ExternalServerException;
import com.example.beauty_salon.exception.UserNotFoundException;
import feign.Request;
import feign.Response;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;

class UserServiceErrorDecoderTest {

  private final UserServiceErrorDecoder decoder = new UserServiceErrorDecoder();

  private Response mockResponse(int status) {
    return Response.builder()
        .status(status)
        .reason("Error")
        .request(Request.create(
            Request.HttpMethod.GET,
            "/test",
            Collections.emptyMap(),
            null,
            StandardCharsets.UTF_8,
            null))
        .build();
  }

  @Test
  void testDecode_404_ShouldReturnUserNotFoundException() {
    Response response = mockResponse(404);

    Exception ex = decoder.decode("method", response);

    assertTrue(ex instanceof UserNotFoundException);
    assertEquals("User not found", ex.getMessage());
  }

  @Test
  void testDecode_400_ShouldReturnBadRequestException() {
    Response response = mockResponse(400);

    Exception ex = decoder.decode("method", response);

    assertTrue(ex instanceof BadRequestException);
    assertEquals("Bad request to user service", ex.getMessage());
  }

  @Test
  void testDecode_500_ShouldReturnExternalServerException() {
    Response response = mockResponse(500);

    Exception ex = decoder.decode("method", response);

    assertTrue(ex instanceof ExternalServerException);
    assertEquals("User service internal error", ex.getMessage());
  }

  @Test
  void testDecode_Default_ShouldUseDefaultDecoder() {
    Response response = mockResponse(418); // I'm a teapot 😄

    Exception ex = decoder.decode("method", response);

    assertTrue(ex.getClass().getSimpleName().contains("Feign"));
  }
}