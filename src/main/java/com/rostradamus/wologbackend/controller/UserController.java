package com.rostradamus.wologbackend.controller;

import com.rostradamus.wologbackend.model.User;
import com.rostradamus.wologbackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  UserRepository userRepository;

  @GetMapping
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<?> getUsers() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<?> getUser(@PathVariable long id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return ResponseEntity.ok(user);
  }
}
