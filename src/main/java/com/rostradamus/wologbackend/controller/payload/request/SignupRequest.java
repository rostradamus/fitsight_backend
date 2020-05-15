package com.rostradamus.wologbackend.controller.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignupRequest {
  @NotBlank
  private String email;

  @NotBlank
  @Size(min = 8, max = 32)
  private String password;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;
}
