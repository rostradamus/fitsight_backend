package com.rostradamus.wologbackend.model;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "plans")
public class Plan extends AuditModel {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Temporal(TemporalType.TIMESTAMP)
  private Date start_at;

  @NotBlank
  @Temporal(TemporalType.TIMESTAMP)
  private Date end_at;
}
