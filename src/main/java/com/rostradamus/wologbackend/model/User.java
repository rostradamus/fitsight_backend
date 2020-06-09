package com.rostradamus.wologbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "users",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
  })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @NotBlank
  @Size(max = 50)
  @Email
  protected String email;

  @NotBlank
  @Size(max = 100)
  protected String firstName;

  @NotBlank
  @Size(max = 100)
  protected String lastName;
}
