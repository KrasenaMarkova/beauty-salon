package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.exception.UserAlreadyExistsException;
import com.example.beauty_salon.exception.UserNotFoundException;
import java.nio.file.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ModelAndView handleException(UserNotFoundException e) {

    ModelAndView modelAndView = new ModelAndView("not-found");

    return modelAndView;
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public String handleUserAlreadyExistsException(UserAlreadyExistsException e, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errorMessage", "Потребителското име или email вече съществуват");
    return "redirect:/register";

  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({NoResourceFoundException.class, AccessDeniedException.class})
  public ModelAndView handleSpringException() {

    ModelAndView modelAndView = new ModelAndView("not-found");

    return modelAndView;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ModelAndView handleLeftoverExceptions(Exception e) {

    ModelAndView modelAndView = new ModelAndView("not-found");

    return modelAndView;
  }

}
