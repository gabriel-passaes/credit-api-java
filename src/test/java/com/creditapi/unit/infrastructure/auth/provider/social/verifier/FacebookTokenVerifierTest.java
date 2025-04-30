package com.creditapi.unit.infrastructure.auth.provider.social.verifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.creditapi.infrastructure.auth.provider.social.config.FacebookProperties;
import com.creditapi.infrastructure.auth.provider.social.model.FacebookProfile;
import com.creditapi.infrastructure.auth.provider.social.verifier.FacebookTokenVerifier;

class FacebookTokenVerifierTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private FacebookProperties facebookProperties;

  @InjectMocks
  private FacebookTokenVerifier facebookTokenVerifier;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(facebookProperties.getAppId()).thenReturn("fake-facebook-app-id");
    when(facebookProperties.getAppSecret()).thenReturn("fake-facebook-app-secret");

    facebookTokenVerifier = new FacebookTokenVerifier(facebookProperties, restTemplate);
  }

  @Test
  @DisplayName("Deve retornar perfil simulado quando ambiente não é produção")
  void shouldReturnMockProfile_whenNotProd() {
    FacebookProperties mockProperties = new FacebookProperties();
    mockProperties.setAppId("fake-facebook-app-id");
    mockProperties.setAppSecret("fake-facebook-app-secret");

    FacebookTokenVerifier verifier = new FacebookTokenVerifier(mockProperties, restTemplate);

    Optional<FacebookProfile> result = verifier.verifyAndGetProfile("mock-token");

    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("mock-facebook@example.com");
    assertThat(result.get().getName()).isEqualTo("Mock Facebook User");
  }

  @Test
  @DisplayName("Deve retornar perfil válido quando token é válido em ambiente de produção")
  void shouldReturnProfile_whenValidTokenAndProd() {
    String token = "valid-token";

    Map<String, Object> debugData = Map.of(
        "app_id", "fake-facebook-app-id",
        "is_valid", true
    );
    Map<String, Object> debugResponseBody = Map.of("data", debugData);

    Map<String, Object> profileResponseBody = Map.of(
        "email", "user@example.com",
        "name", "User Test"
    );

    when(restTemplate.exchange(
        eq("https://graph.facebook.com/debug_token?input_token=" + token + "&access_token=fake-facebook-app-id|fake-facebook-app-secret"),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class))
    ).thenReturn(new ResponseEntity<>(debugResponseBody, HttpStatus.OK));

    when(restTemplate.exchange(
        eq("https://graph.facebook.com/me?fields=name,email&access_token=" + token),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class))
    ).thenReturn(new ResponseEntity<>(profileResponseBody, HttpStatus.OK));

    Optional<FacebookProfile> result = facebookTokenVerifier.verifyAndGetProfile(token);

    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("user@example.com");
    assertThat(result.get().getName()).isEqualTo("User Test");
  }

  @Test
  @DisplayName("Deve retornar vazio quando token inválido")
  void shouldReturnEmpty_whenInvalidToken() {
    String token = "invalid-token";

    when(restTemplate.exchange(
        eq("https://graph.facebook.com/debug_token?input_token=" + token + "&access_token=fake-facebook-app-id|fake-facebook-app-secret"),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class))
    ).thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

    Optional<FacebookProfile> result = facebookTokenVerifier.verifyAndGetProfile(token);

    assertThat(result).isEmpty();
  }
}
