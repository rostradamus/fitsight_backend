package com.rostradamus.fitsight.backend.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "plans")
@EqualsAndHashCode(callSuper = false)
@Data
public class Plan extends AuditModel {
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
