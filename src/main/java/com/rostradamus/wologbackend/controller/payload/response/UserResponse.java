package com.rostradamus.wologbackend.controller.payload.response;

import com.rostradamus.wologbackend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserResponse {
  private long email;
  private String firstName;
  private String lastName;
  private Set<Role> roles;
}
