package com.example.beauty_salon.web.controller;


import com.example.beauty_salon.user.model.User;
import com.example.beauty_salon.user.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

  private final UserService userService;

  @Autowired
  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ModelAndView getAllUsers() {

    List<User> users = userService.getAll();

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("users");
    modelAndView.addObject("users", users);

    return modelAndView;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/toggle-status")
  public String toggleUserStatus(@PathVariable UUID id) {

    userService.toggleUserStatus(id);

    return "redirect:/admin/users";
  }

  @PostMapping("/{id}/delete")
  public String deleteUser(@PathVariable UUID id) {

    userService.deleteById(id);

    return "redirect:/admin/users";
  }

}
