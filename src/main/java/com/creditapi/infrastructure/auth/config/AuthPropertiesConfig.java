package com.creditapi.infrastructure.auth.config;

import com.creditapi.infrastructure.auth.provider.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AuthPropertiesConfig {}
