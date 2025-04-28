package com.creditapi.infrastructure.auth.provider.social.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FacebookProfile {
  private String email;
  private String name;
}
