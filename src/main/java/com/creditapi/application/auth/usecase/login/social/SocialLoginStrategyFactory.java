package com.creditapi.application.auth.usecase.login.social;

import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SocialLoginStrategyFactory {

  private final Map<SocialProvider, SocialLoginStrategy> strategies;

  public SocialLoginStrategyFactory(List<SocialLoginStrategy> strategyList) {
    this.strategies =
        strategyList.stream().collect(Collectors.toMap(SocialLoginStrategy::getProvider, s -> s));
  }

  public SocialLoginStrategy get(SocialProvider provider) {
    var strategy = strategies.get(provider);
    if (strategy == null) {
      throw new SocialLoginException("Provedor social não suportado: " + provider);
    }
    return strategy;
  }
}
