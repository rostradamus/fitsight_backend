package com.rostradamus.fitsight.backend.controller.payload.response;

import com.rostradamus.fitsight.backend.model.User;
import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String email;
  private User user;
  private List<String> roles;

  public JwtResponse(String token, Long id, String email, List<String> roles, User user) {
    this.token = token;
    this.id = id;
    this.email = email;
    this.roles = roles;
    this.user = user;
  }
}
