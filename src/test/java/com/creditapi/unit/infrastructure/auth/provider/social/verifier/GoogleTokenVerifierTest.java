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

import com.creditapi.infrastructure.auth.provider.social.config.GoogleProperties;
import com.creditapi.infrastructure.auth.provider.social.model.GoogleProfile;
import com.creditapi.infrastructure.auth.provider.social.verifier.GoogleTokenVerifier;

class GoogleTokenVerifierTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private GoogleProperties googleProperties;

  @InjectMocks
  private GoogleTokenVerifier googleTokenVerifier;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(googleProperties.getClientId()).thenReturn("fake-google-client-id");

    googleTokenVerifier = new GoogleTokenVerifier(googleProperties, restTemplate);
  }

  @Test
  @DisplayName("Deve retornar perfil simulado quando ambiente não é produção")
  void shouldReturnMockProfile_whenNotProd() {
    GoogleProperties mockProperties = new GoogleProperties();
    mockProperties.setClientId("fake-google-client-id");
    mockProperties.setClientSecret("fake-google-client-secret");

    GoogleTokenVerifier verifier = new GoogleTokenVerifier(mockProperties, restTemplate);

    Optional<GoogleProfile> result = verifier.verifyAndGetPayload("mock-token");

    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("mock-google@example.com");
    assertThat(result.get().getName()).isEqualTo("Mock Google User");
  }

  @Test
  @DisplayName("Deve retornar perfil válido quando token e ambiente de produção são válidos")
  void shouldReturnProfile_whenValidTokenAndProd() {
    String token = "valid-token";

    Map<String, Object> fakeResponse = Map.of(
        "aud", "fake-google-client-id",
        "email", "user@example.com",
        "name", "User Test"
    );

    when(restTemplate.exchange(
        eq("https://oauth2.googleapis.com/tokeninfo?id_token=" + token),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class))
    ).thenReturn(new ResponseEntity<>(fakeResponse, HttpStatus.OK));

    Optional<GoogleProfile> result = googleTokenVerifier.verifyAndGetPayload(token);

    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("user@example.com");
    assertThat(result.get().getName()).isEqualTo("User Test");
  }

  @Test
  @DisplayName("Deve retornar vazio quando token for inválido")
  void shouldReturnEmpty_whenInvalidToken() {
    String token = "invalid-token";

    when(restTemplate.exchange(
        eq("https://oauth2.googleapis.com/tokeninfo?id_token=" + token),
        eq(HttpMethod.GET),
        eq(null),
        any(ParameterizedTypeReference.class))
    ).thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

    Optional<GoogleProfile> result = googleTokenVerifier.verifyAndGetPayload(token);

    assertThat(result).isEmpty();
  }
}
