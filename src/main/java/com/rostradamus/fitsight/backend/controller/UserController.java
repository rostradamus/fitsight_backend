package com.rostradamus.fitsight.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.rostradamus.fitsight.backend.repository.UserRepository;
import com.rostradamus.fitsight.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  ObjectMapper objectMapper;

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

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<?> editUser(@PathVariable long id, @RequestBody User userRequest) {
    return ResponseEntity.ok(userRepository.save(userRequest));
  }

  @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<?> patchUser(@PathVariable long id, @RequestBody JsonPatch patch) {
    User targetUser = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    try {
      JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
      User patchedUser = objectMapper.treeToValue(patched, User.class);
      return ResponseEntity.ok(userRepository.save(patchedUser));
    } catch (JsonPatchException | JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<?> deleteUser(@PathVariable long id) {
    try {
      userRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.noContent().build();
  }
}
