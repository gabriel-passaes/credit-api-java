package com.creditapi.e2e.common;

import io.cucumber.java.pt.Dado;

public class CommonStepDefinitions {

  @Dado("que o sistema está inicializado")
  public void systemIsInitialized() {
    // Contexto já inicializado pelo SpringBootTest
  }
}
