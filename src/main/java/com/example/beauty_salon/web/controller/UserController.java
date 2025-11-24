package com.example.beauty_salon.web.controller;

import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import com.example.beauty_salon.web.dto.DtoMapper;
import com.example.beauty_salon.web.dto.EditProfileRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{id}/profile")
  public ModelAndView getProfilePage(@PathVariable UUID id) {

    User user = userService.getById(id);
    EditProfileRequest editProfileRequest = DtoMapper.fromUser(user);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("profile-menu");
    modelAndView.addObject("editProfileRequest", editProfileRequest);
    modelAndView.addObject("user", user);

    return modelAndView;
  }

  @PutMapping("/{id}/profile")
  public ModelAndView updateProfile(@Valid EditProfileRequest editProfileRequest, BindingResult bindingResult, @PathVariable UUID id) {

    if (bindingResult.hasErrors()) {
      User user = userService.getById(id);
      ModelAndView modelAndView = new ModelAndView();
      modelAndView.setViewName("profile-menu");
      modelAndView.addObject("user", user);
//      return modelAndView;
    }

    userService.updateProfile(id, editProfileRequest);
    return new ModelAndView("redirect:/home");
  }

}
