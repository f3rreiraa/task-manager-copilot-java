# Gerenciador de Tarefas

Gerenciador de tarefas full stack desenvolvido com **GitHub Copilot** (VS Code) na atividade de laboratório (Java 21 + Spring Boot + H2 + HTML/CSS/JS).

**O que foi implementado com o GitHub Copilot em cima do projeto base:**
- prioridade das tarefas (`LOW`, `MEDIUM`, `HIGH`);
- busca por título (ignora maiúsculas/minúsculas) e filtro por status: `GET /api/tasks?title=copilot&status=PENDING`;
- respostas de erro sempre em JSON com status HTTP certo (400, 404, 405, 500);
- testes de service, repository e controller (28 testes).

**Como rodar:**
```bash
mvn test
mvn spring-boot:run
```
Depois abra `http://localhost:8080`.

O relatório da atividade está em [`RELATORIO.md`](RELATORIO.md). Abaixo está o roteiro original do laboratório.

---

## Laboratório: Java Full Stack com VS Code, GitHub Copilot, AGENTS.md e Skills

Projeto didático para aprender a usar o GitHub Copilot como assistente e agente de engenharia, com planejamento, implementação, testes, depuração, revisão e Git.

## Resultado final
Um gerenciador de tarefas com backend Java/Spring Boot, API REST, banco H2 e frontend HTML/CSS/JavaScript.

## Pré-requisitos
- Java 21 (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Git (`git --version`)
- VS Code
- Extensões: Extension Pack for Java, GitHub Copilot e GitHub Copilot Chat
- Conta GitHub com acesso ao Copilot

## Estrutura
```text
.github/
  copilot-instructions.md
  skills/
    backend-java/SKILL.md
    frontend/SKILL.md
    testing/SKILL.md
    code-review/SKILL.md
AGENTS.md
src/main/java/...       backend
src/main/resources/static frontend
src/test/java/...       testes
```

## Passo 1: obter e abrir o projeto
```bash
git clone URL_DO_REPOSITORIO
git checkout -b aluno/seu-nome
code .
```
Se o professor entregar um ZIP, extraia a pasta, abra no VS Code e execute `git init`.

## Passo 2: verificar o ambiente
No terminal integrado:
```bash
java -version
mvn -version
git --version
mvn test
```
O Java deve estar na versão 21 e os testes devem terminar com `BUILD SUCCESS`.

## Passo 3: estudar o contexto do agente
Leia, nesta ordem:
1. `AGENTS.md`
2. `.github/copilot-instructions.md`
3. `.github/skills/backend-java/SKILL.md`
4. `.github/skills/frontend/SKILL.md`
5. `.github/skills/testing/SKILL.md`
6. `.github/skills/code-review/SKILL.md`

No Copilot Chat, use:
```text
Leia AGENTS.md e .github/copilot-instructions.md. Não altere arquivos.
Explique a arquitetura do projeto, as regras de domínio e quando cada Skill deve ser usada.
```

## Passo 4: explorar o backend
Prompt:
```text
Use as regras de AGENTS.md e a Skill backend-java.
Não altere arquivos. Explique o fluxo completo de POST /api/tasks,
do JSON recebido até a gravação no H2, citando as classes envolvidas.
```
Localize `TaskController`, `TaskService`, `TaskRepository`, `Task`, DTOs e tratamento de erros.

## Passo 5: executar a aplicação
```bash
mvn spring-boot:run
```
Abra `http://localhost:8080`.
Opcional: console H2 em `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:taskdb`, usuário `sa`, senha vazia.

Teste criar, editar, concluir e excluir uma tarefa.

## Passo 6: testar a API
Exemplo com curl:
```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Estudar Copilot","description":"Concluir o laboratório","status":"PENDING"}'

curl -i http://localhost:8080/api/tasks
```
Teste também título com menos de 3 caracteres e confirme o retorno HTTP 400.

## Passo 7: mudança orientada por plano
Requisito: adicionar prioridade `LOW`, `MEDIUM`, `HIGH`.

Primeiro prompt:
```text
Leia AGENTS.md e use backend-java, frontend e testing quando aplicável.
Novo requisito: toda tarefa deve possuir prioridade LOW, MEDIUM ou HIGH.
Não implemente ainda. Analise impactos em entidade, DTOs, serviço, API,
frontend, validação e testes. Apresente um plano incremental e os arquivos afetados.
```
Revise o plano. Depois use:
```text
Implemente o plano em pequenas etapas. Preserve a arquitetura.
Ao final de cada etapa, compile ou execute os testes relevantes.
Não adicione frameworks nem dependências desnecessárias.
```

## Passo 8: testes
Prompt:
```text
Use a Skill testing. Revise TaskServiceTest e crie testes para a prioridade.
Use Arrange, Act, Assert. Não enfraqueça asserções existentes.
Execute mvn test e explique eventuais falhas.
```
Depois execute:
```bash
mvn test
```

## Passo 9: depuração controlada
Altere temporariamente, em `app.js`, `/api/tasks` para `/api/task`. Recarregue a página e observe o erro.

Prompt:
```text
A listagem deixou de funcionar. Não altere nada ainda.
Investigue frontend e backend, identifique causa raiz, apresente evidência
e proponha a menor correção. Aguarde minha autorização para editar.
```
Após o diagnóstico, autorize a correção e confirme o funcionamento. Não faça commit do erro proposital.

## Passo 10: code review
```bash
git diff
```
Prompt:
```text
Use a Skill code-review. Analise somente o git diff atual e não edite arquivos.
Classifique achados em CRITICAL, HIGH, MEDIUM, LOW ou SUGGESTION.
Para cada achado, mostre arquivo, evidência, impacto e correção recomendada.
Verifique aderência ao AGENTS.md e testes faltantes.
```

## Passo 11: commit
```bash
git status
git add .
git commit -m "feat: add task priority"
git log --oneline -5
```
Nunca execute `git add .` sem revisar `git diff` e `git status`.

## Desafio final
Implemente busca por título e filtro por status. A dupla deve conduzir todo o ciclo:
1. interpretar requisito;
2. selecionar Skills;
3. pedir análise e plano;
4. implementar incrementalmente;
5. executar testes;
6. revisar o `git diff`;
7. fazer commit.

Critérios de aceite:
- `GET /api/tasks?title=copilot&status=PENDING` funciona;
- filtros podem ser usados separadamente;
- busca por título ignora maiúsculas/minúsculas;
- filtros aparecem no frontend;
- testes cobrem combinações principais;
- nenhum Controller acessa Repository diretamente.

## Entrega
Entregar:
- link do repositório;
- branch ou Pull Request;
- aplicação funcional;
- testes passando;
- arquivo `RELATORIO.md` com prompts usados, decisões aceitas/rejeitadas, erro investigado e aprendizados.

## Avaliação
| Critério | Pontos |
|---|---:|
| Aplicação e API | 20 |
| Arquitetura | 15 |
| Frontend | 10 |
| Testes | 15 |
| Uso de AGENTS.md | 10 |
| Uso das Skills | 10 |
| Planejamento e prompts | 10 |
| Code review, Git e relatório | 10 |
| Total | 100 |

## Regras de uso responsável
- Não aceitar mudanças sem entender o diff.
- Não enviar credenciais ao chat nem ao repositório.
- Não permitir comandos destrutivos sem revisão.
- O aluno continua responsável por arquitetura, qualidade e resultado.
