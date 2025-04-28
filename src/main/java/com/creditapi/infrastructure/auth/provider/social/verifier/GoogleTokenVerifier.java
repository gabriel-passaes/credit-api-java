package com.creditapi.infrastructure.auth.provider.social.verifier;

import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.creditapi.infrastructure.auth.provider.social.config.GoogleProperties;
import com.creditapi.infrastructure.auth.provider.social.model.GoogleProfile;

@Component
public class GoogleTokenVerifier {

  private final GoogleProperties googleProperties;
  private final boolean isProd;
  private final RestTemplate restTemplate;

  public GoogleTokenVerifier(GoogleProperties googleProperties, RestTemplate restTemplate) {
    this.googleProperties = googleProperties;
    this.restTemplate = restTemplate;
    this.isProd = "prod".equalsIgnoreCase(System.getProperty("spring.profiles.active", "dev"));
  }

  public Optional<GoogleProfile> verifyAndGetPayload(String idToken) {
    if (!isProd) {
      return Optional.of(new GoogleProfile("mock-google@example.com", "Mock Google User"));
    }

    try {
      String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

      ResponseEntity<Map<String, Object>> response =
          restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

      if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
        return Optional.empty();
      }

      Map<String, Object> payload = response.getBody();
      String aud = (String) payload.get("aud");

      if (!googleProperties.getClientId().equals(aud)) {
        return Optional.empty();
      }

      String email = (String) payload.get("email");
      String name = (String) payload.get("name");

      return Optional.of(new GoogleProfile(email, name));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
