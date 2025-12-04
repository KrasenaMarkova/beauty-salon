package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.exception.NoFreeEmployeeException;
import com.example.beauty_salon.exception.PasswordDoNotMatchException;
import com.example.beauty_salon.exception.UserAlreadyExistsException;
import com.example.beauty_salon.exception.UserException;
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
  @ExceptionHandler(UserException.class)
  public ModelAndView handleException(UserException e) {

    ModelAndView modelAndView = new ModelAndView("not-found");
    modelAndView.addObject("errorMessage", e.getMessage());

    return modelAndView;
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public String handleUserAlreadyExistsException(UserAlreadyExistsException e, RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", "Потребителското име или email вече съществуват.");

    return "redirect:/register";
  }

  @ExceptionHandler(PasswordDoNotMatchException.class)
  public String handlePasswordDoNotMatchException(PasswordDoNotMatchException e, RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", "Паролите не съвпадат. Моля, въведете ги отново.");

    return "redirect:/register";
  }

  @ExceptionHandler(NoFreeEmployeeException.class)
  public String handleNoFreeEmployeeException(NoFreeEmployeeException e, RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", "Служителят е зает в този час.");

    return "redirect:/appointments/{id}/edit";
  }

  @ExceptionHandler({IllegalArgumentException.class, SecurityException.class})
  public String handleEditingExceptions(
      RuntimeException ex,
      RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

    return "redirect:/home";
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
}
