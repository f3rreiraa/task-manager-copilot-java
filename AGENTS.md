# AGENTS.md

## Missão
Construir e manter um gerenciador de tarefas educacional full stack.

## Stack obrigatória
- Java 21, Spring Boot, Maven
- Spring Web, Spring Data JPA, Bean Validation e H2
- HTML5, CSS3, JavaScript e Fetch API
- JUnit 5 e Mockito

## Arquitetura
Controller -> Service -> Repository -> H2

- Controller não acessa Repository diretamente.
- Regras de negócio ficam no Service.
- Entrada e saída da API usam DTOs.
- Usar injeção por construtor.

## Contrato
Base: `/api/tasks`
- GET `/api/tasks`
- GET `/api/tasks/{id}`
- POST `/api/tasks`
- PUT `/api/tasks/{id}`
- DELETE `/api/tasks/{id}`

## Regras de domínio
- title: obrigatório, entre 3 e 100 caracteres.
- description: máximo de 500 caracteres.
- status: PENDING, IN_PROGRESS ou DONE.

## Segurança e qualidade
- Nunca incluir segredos ou credenciais.
- Não expor stack traces ao frontend.
- Não alterar dependências sem explicar o motivo.
- Antes de uma mudança relevante: analisar, propor plano e implementar incrementalmente.
- Ao final: compilar, testar e resumir alterações.

## Definition of Done
1. Compila com Java 21.
2. Testes passam.
3. Entradas são validadas.
4. Erros retornam JSON e status HTTP adequado.
5. Frontend consome a API.
6. `git diff` foi revisado.
