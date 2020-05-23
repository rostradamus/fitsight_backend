package com.rostradamus.wologbackend.security.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@RedisHash("spring:refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken implements Serializable {
  @Id
  private String id;

  private String username;


  @Value("${rostradamus.app.refreshTokenTimeToLive}")
  @TimeToLive
  private Long expiration;
}
