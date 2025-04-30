package com.creditapi.infrastructure.auth.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.creditapi.infrastructure.auth.provider.middleware.JwtAuthenticationFilter;

import jakarta.servlet.Filter;

@Configuration
@Profile("test")
public class SecurityTestConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final Filter rateLimiterFilter;

  public SecurityTestConfig(
      JwtAuthenticationFilter jwtFilter,
      @Qualifier("rateLimiterFilter") Filter rateLimiterFilter) {
    this.jwtFilter = jwtFilter;
    this.rateLimiterFilter = rateLimiterFilter;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/**",
                "/user/**",
                "/credit/**",
                "/invoice/**",
                "/h2-console/**",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ).permitAll()
            .anyRequest().permitAll()
        )
        .addFilterBefore(rateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
