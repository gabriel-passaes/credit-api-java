package com.creditapi.infrastructure.auth.provider.social.verifier;

import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.creditapi.infrastructure.auth.provider.social.config.FacebookProperties;
import com.creditapi.infrastructure.auth.provider.social.model.FacebookProfile;

@Component
public class FacebookTokenVerifier {

  private final FacebookProperties facebookProperties;
  private final boolean isProd;
  private final RestTemplate restTemplate;

  public FacebookTokenVerifier(FacebookProperties facebookProperties, RestTemplate restTemplate) {
    this.facebookProperties = facebookProperties;
    this.restTemplate = restTemplate;
    this.isProd = "prod".equalsIgnoreCase(System.getProperty("spring.profiles.active", "dev"));
  }

  public Optional<FacebookProfile> verifyAndGetProfile(String userToken) {
    if (!isProd) {
      return Optional.of(new FacebookProfile("mock-facebook@example.com", "Mock Facebook User"));
    }

    try {
      String appAccessToken = facebookProperties.getAppId() + "|" + facebookProperties.getAppSecret();
      String debugUrl = "https://graph.facebook.com/debug_token?input_token=" + userToken + "&access_token=" + appAccessToken;

      ResponseEntity<Map<String, Object>> debugResponse =
          restTemplate.exchange(debugUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

      if (debugResponse.getStatusCode() != HttpStatus.OK || debugResponse.getBody() == null) {
        return Optional.empty();
      }

      Map<String, Object> body = debugResponse.getBody();
      Object dataObj = body.get("data");

      if (!(dataObj instanceof Map<?, ?> rawData)) {
        return Optional.empty();
      }

      Map<?, ?> data = rawData;

      if (!facebookProperties.getAppId().equals(data.get("app_id")) || !(Boolean.TRUE.equals(data.get("is_valid")))) {
        return Optional.empty();
      }

      String profileUrl = "https://graph.facebook.com/me?fields=name,email&access_token=" + userToken;

      ResponseEntity<Map<String, Object>> profileResponse =
          restTemplate.exchange(profileUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

      if (profileResponse.getStatusCode() != HttpStatus.OK || profileResponse.getBody() == null) {
        return Optional.empty();
      }

      Map<String, Object> profile = profileResponse.getBody();
      String email = (String) profile.get("email");
      String name = (String) profile.get("name");

      return Optional.of(new FacebookProfile(email, name));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
