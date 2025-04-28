package com.creditapi.application.auth.usecase.login.social;

import com.creditapi.domain.auth.exception.SocialLoginException;
import com.creditapi.domain.user.model.User;
import com.creditapi.infrastructure.auth.provider.social.SocialProvider;

public interface SocialLoginStrategy {

  SocialProvider getProvider();

  User authenticate(String socialToken) throws SocialLoginException;
}
