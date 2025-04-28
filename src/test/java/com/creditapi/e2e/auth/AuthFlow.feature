Feature: Fluxo de Autenticação

  Background:
    Given que o sistema de autenticação está inicializado

  Scenario: Login com credenciais válidas
    Given que existe um usuário cadastrado no sistema de autenticação
    When eu envio email e senha válidos para autenticação
    Then o sistema de autenticação deve retornar status 200 e um token JWT válido

  Scenario: Login com senha inválida
    Given que existe um usuário cadastrado no sistema de autenticação
    When eu envio o email correto e senha incorreta para autenticação
    Then o sistema de autenticação deve retornar status 401

  Scenario: Login com email inexistente
    When eu envio email de usuário não cadastrado para autenticação
    Then o sistema de autenticação deve retornar status 404

  Scenario: Refresh token válido
    Given que tenho um refresh token válido de autenticação
    When eu solicito um novo access token de autenticação
    Then o sistema de autenticação deve retornar status 200 e um novo token JWT
