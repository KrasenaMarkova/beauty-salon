package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.exception.NoFreeEmployeeException;
import com.example.beauty_salon.exception.UserAlreadyExistsException;
import com.example.beauty_salon.exception.UserNotFoundException;
import com.example.beauty_salon.web.dto.AppointmentRequest;
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

  @ExceptionHandler({IllegalArgumentException.class, SecurityException.class})
  public String handleEditingExceptions(
      RuntimeException ex,
      RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
    return "redirect:/home";
  }

  @ExceptionHandler({NoFreeEmployeeException.class})
  public String handleNoFreeEmployeeExceptions(
      RuntimeException ex,
      RedirectAttributes redirectAttributes) {
    //modelAndView.addObject("success", "Часът беше успешно запазен!");
    //modelAndView.addObject("appointmentRequest", new AppointmentRequest());
    redirectAttributes.addFlashAttribute("success", ex.getMessage());
    redirectAttributes.addFlashAttribute("appointmentRequest", new AppointmentRequest());
    return "redirect:/booking";
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({NoResourceFoundException.class, AccessDeniedException.class})
  public ModelAndView handleSpringException() {

    ModelAndView modelAndView = new ModelAndView("not-found");

    return modelAndView;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ModelAndView handleLeftoverExceptions(Exception e, RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    ModelAndView modelAndView = new ModelAndView("not-found");

    return modelAndView;
  }

  /*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ModelAndView handleLeftoverExceptions(Exception e) {

    ModelAndView modelAndView = new ModelAndView("not-found");

    return modelAndView;
  }*/

}
