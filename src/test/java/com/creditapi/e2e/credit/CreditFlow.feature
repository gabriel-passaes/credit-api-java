Feature: Fluxo de Crédito

  Background:
    Given que o sistema de crédito está inicializado

  Scenario: Criar crédito com dados válidos
    When eu envio os dados válidos para criar um crédito
    Then o sistema de crédito deve retornar status 201 e os dados do crédito criado

  Scenario: Buscar crédito existente
    Given que existe um crédito salvo com número CREDIT001 no sistema de crédito
    When eu faço uma requisição para buscar o crédito por número
    Then o sistema de crédito deve retornar status 200 e os dados corretos do crédito

  Scenario: Criar crédito com valor inválido
    When eu envio um payload com valor inválido para o crédito
    Then o sistema de crédito deve retornar status 400 e uma mensagem de erro

  Scenario: Buscar crédito inexistente
    When eu faço uma requisição para buscar um crédito com número inexistente
    Then o sistema de crédito deve retornar status 404
