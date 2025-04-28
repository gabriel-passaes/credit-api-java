package com.creditapi.e2e.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

  @Autowired private ApplicationContext applicationContext;

  @PostConstruct
  public void printBeans() {
    System.out.println("\n=== BEANS CARREGADOS NO SPRING CONTEXT ===");
    Arrays.stream(applicationContext.getBeanDefinitionNames())
        .sorted()
        .forEach(System.out::println);
  }
}