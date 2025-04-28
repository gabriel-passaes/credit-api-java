package com.creditapi.infrastructure.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@Profile("!test") 
public class RateLimiterConfig {

  @Bean(name = "rateLimiterFilter")
  public OncePerRequestFilter rateLimiterFilter(StringRedisTemplate redisTemplate) {
    return new OncePerRequestFilter() {
      private static final int MAX_REQUESTS_PER_MINUTE = 30;
      private static final String PREFIX = "rate-limit:";

      @Override
      protected void doFilterInternal(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        String key = PREFIX + ip;

        Long requestCount = redisTemplate.opsForValue().increment(key);
        if (requestCount != null && requestCount == 1L) {
          redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (requestCount != null && requestCount > MAX_REQUESTS_PER_MINUTE) {
          response.setStatus(429);
          response.getWriter().write("⚠️ Too many requests – try again later.");
          return;
        }

        filterChain.doFilter(request, response);
      }
    };
  }
}
