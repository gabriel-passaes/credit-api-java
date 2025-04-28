package com.creditapi.unit.infrastructure.auth.provider.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategy;
import com.creditapi.application.auth.usecase.login.social.SocialLoginStrategyFactory;
import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;

class SocialLoginStrategyFactoryTest {

  private SocialLoginStrategy googleStrategy;
  private SocialLoginStrategy facebookStrategy;
  private SocialLoginStrategyFactory factory;

  @BeforeEach
  void setUp() {
    googleStrategy = mock(SocialLoginStrategy.class);
    facebookStrategy = mock(SocialLoginStrategy.class);

    // Mockando retorno dos providers
    when(googleStrategy.getProvider()).thenReturn(SocialProvider.GOOGLE);
    when(facebookStrategy.getProvider()).thenReturn(SocialProvider.FACEBOOK);

    factory = new SocialLoginStrategyFactory(List.of(googleStrategy, facebookStrategy));
  }

  @Test
  @DisplayName("deve retornar estratégia correta para provedor GOOGLE")
  void shouldReturnGoogleStrategy_whenProviderIsGoogle() {
    SocialLoginStrategy result = factory.get(SocialProvider.GOOGLE);

    assertThat(result).isEqualTo(googleStrategy);
  }

  @Test
  @DisplayName("deve retornar estratégia correta para provedor FACEBOOK")
  void shouldReturnFacebookStrategy_whenProviderIsFacebook() {
    SocialLoginStrategy result = factory.get(SocialProvider.FACEBOOK);

    assertThat(result).isEqualTo(facebookStrategy);
  }

  @Test
  @DisplayName("deve lançar exceção ao receber provedor social não suportado")
  void shouldThrowException_whenProviderIsUnsupported() {
    assertThatThrownBy(() -> factory.get(null))
        .isInstanceOf(SocialLoginException.class)
        .hasMessageContaining("Provedor social não suportado");
  }
}
