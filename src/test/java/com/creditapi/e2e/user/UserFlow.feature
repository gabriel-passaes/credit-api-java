Feature: Fluxo de Usuário

  Background:
    Given que o sistema de usuários está inicializado

  Scenario: Criar usuário com dados válidos
    When eu envio os dados válidos para criar um usuário
    Then o sistema deve retornar status 201 e os dados do usuário criado

  Scenario: Buscar usuário existente
    Given que existe um usuário salvo com ID 1
    When eu faço uma requisição para buscar o usuário por ID
    Then o sistema deve retornar status 200 e os dados corretos do usuário

  Scenario: Criar usuário com email inválido
    When eu envio um payload com email inválido
    Then o sistema deve retornar status 400 e uma mensagem de erro

  Scenario: Buscar usuário inexistente
    When eu faço uma requisição para buscar o usuário com ID inexistente
    Then o sistema deve retornar status 404 de usuário não encontrado
