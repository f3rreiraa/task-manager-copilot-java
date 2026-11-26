---
name: testing
description: criar e revisar testes automatizados Java com JUnit 5 e Mockito; usar ao testar services, validações, erros, regressões e novos comportamentos
---
# Regras
- Estruturar cada teste em Arrange, Act, Assert.
- Nomear testes pelo comportamento, como `shouldCreateTaskWhenInputIsValid`.
- Cobrir caminho feliz, entrada inválida, item inexistente, atualização e exclusão.
- Não enfraquecer asserções para fazer o teste passar.
- Executar `mvn test` ao final.
