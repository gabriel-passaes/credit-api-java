package com.creditapi.infrastructure.auth.security;

import com.creditapi.domain.user.model.Role;
import com.creditapi.domain.user.model.User;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

  public Optional<User> getAuthenticatedUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof User user) {
      return Optional.of(user);
    }

    return Optional.empty();
  }

  public User getRequiredUser() {
    return getAuthenticatedUser()
        .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
  }

  public boolean isSuperAdmin() {
    return getAuthenticatedUser().map(user -> user.getRole() == Role.SUPER_ADMIN).orElse(false);
  }

  public boolean isAdmin() {
    return getAuthenticatedUser().map(user -> user.getRole() == Role.ADMIN).orElse(false);
  }

  public boolean isUser() {
    return getAuthenticatedUser().map(user -> user.getRole() == Role.USER).orElse(false);
  }
}
