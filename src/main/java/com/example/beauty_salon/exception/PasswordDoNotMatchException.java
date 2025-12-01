package com.example.beauty_salon.exception;

public class PasswordDoNotMatchException extends RuntimeException {

  public PasswordDoNotMatchException(String message) {
    super(message);
  }
}
