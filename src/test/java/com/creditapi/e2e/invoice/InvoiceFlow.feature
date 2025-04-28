Feature: Fluxo de Nota Fiscal

  Background:
    Given que existe uma nota fiscal salva com ID 1

  Scenario: Download de nota fiscal existente
    When eu faço uma requisição para baixar a nota
    Then o sistema deve retornar status 200 e um PDF válido

  Scenario: Download de nota fiscal inexistente
    When eu faço uma requisição para baixar a nota com ID inválido
    Then o sistema deve retornar status 404 de nota fiscal não encontrada

  Scenario: Upload de arquivo PDF válido
    When eu faço upload de um arquivo PDF válido para a nota com ID 1
    Then o sistema deve retornar status 200 e uma confirmação

  Scenario: Upload de arquivo inválido
    When eu faço upload de um arquivo .txt para a nota com ID 1
    Then o sistema deve retornar status 400 com mensagem de erro
