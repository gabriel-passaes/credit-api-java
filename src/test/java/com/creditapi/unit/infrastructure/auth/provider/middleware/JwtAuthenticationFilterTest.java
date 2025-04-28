package com.creditapi.unit.infrastructure.auth.provider.middleware;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.creditapi.infrastructure.auth.provider.jwt.JwtProperties;
import com.creditapi.infrastructure.auth.provider.jwt.JwtTokenProvider;
import com.creditapi.infrastructure.auth.provider.middleware.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class JwtAuthenticationFilterTest {

  @Test
  @DisplayName("deve autenticar usuário a partir de token válido")
  void shouldAuthenticateUserFromValidToken() throws ServletException, IOException {
    JwtProperties props = new JwtProperties();
    props.setSecret("mysupersecretkey1234567890mysupersecretkey1234567890");
    props.setAccessTokenTtl(Duration.ofMinutes(30));
    props.setRefreshTokenTtl(Duration.ofMinutes(30));

    JwtTokenProvider provider = new JwtTokenProvider(props);
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);

    String token = provider.generateAccessToken(
        com.creditapi.domain.user.model.User.builder()
            .id(123L)
            .email("test@example.com")
            .name("Tester")
            .password("123456")
            .build()
    );

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = mock(FilterChain.class);

    filter.doFilter(request, response, chain);

    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    assertThat(auth.getPrincipal()).isEqualTo(123L);

    verify(chain, times(1)).doFilter(request, response);
  }
}
