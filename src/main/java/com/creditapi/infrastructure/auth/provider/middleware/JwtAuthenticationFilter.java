package com.creditapi.infrastructure.auth.provider.middleware;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith("Bearer ")) {
      try {
        var body = jwtProvider.parse(header.substring(7)).getBody();

        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                Long.valueOf(body.getSubject()), null, List.of(() -> "ROLE_USER"));

        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (JwtException e) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT inválido");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
