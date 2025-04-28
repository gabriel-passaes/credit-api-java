package com.creditapi.domain.user.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  private Long id;
  private String name;
  private String email;
  private String document;
  private Role role;
  private String avatarUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String password;

  public User(Long userId) {
    this.id = userId;
  }

  public void update(String name, String email, String document) {
    this.name = name;
    this.email = email;
    this.document = document;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateAvatar(String newAvatarUrl) {
    this.avatarUrl = newAvatarUrl;
    this.updatedAt = LocalDateTime.now();
  }
}
