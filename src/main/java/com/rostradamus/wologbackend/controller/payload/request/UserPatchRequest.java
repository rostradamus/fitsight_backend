package com.rostradamus.wologbackend.controller.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserPatchRequest {
  @NotBlank
  private String email;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;
}
