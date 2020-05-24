package com.rostradamus.wologbackend.security.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.Objects;

@RedisHash(value = "spring:refresh_tokens", timeToLive = 86400)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken implements Serializable {
  @Autowired
  private Environment env;

  @Id
  private String id;

  private String username;

  public RefreshToken(String username) {
    this.username = username;
  }
}
