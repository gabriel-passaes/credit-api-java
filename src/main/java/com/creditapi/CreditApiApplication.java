package com.creditapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.creditapi.infrastructure.auth.provider.social.config.FacebookProperties;
import com.creditapi.infrastructure.auth.provider.social.config.GoogleProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    FacebookProperties.class,
    GoogleProperties.class
})
public class CreditApiApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(CreditApiApplication.class, args);
  }
}
