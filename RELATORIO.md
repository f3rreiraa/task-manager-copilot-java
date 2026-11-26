# Relatório da atividade

## Integrantes
- Víctor Hugo Ferreira Matos — RA: 22605331

Branch: `aluno/f3rreiraa`

Ferramenta utilizada: **GitHub Copilot** (GitHub Copilot Chat no VS Code), usando o `AGENTS.md`, o `.github/copilot-instructions.md` e as Skills de `.github/skills` como contexto. Todo o fluxo da atividade — entendimento do projeto, planejamento, implementação incremental, depuração, testes e code review — foi conduzido no Copilot, seguindo o padrão de ler as regras do projeto, propor um plano e só implementar após aprovação.

## Prompts utilizados

Tentei sempre seguir a mesma estrutura: dizer quais arquivos de contexto ler, qual Skill usar, o que NÃO fazer e qual a saída esperada.

**1. Entendimento do projeto (Passo 3 e 4)**
```text
Leia AGENTS.md, .github/copilot-instructions.md e as Skills em .github/skills. Não altere arquivos.
Explique a arquitetura do projeto, as regras de domínio e quando cada Skill deve ser usada.
Depois explique o fluxo do POST /api/tasks, do JSON recebido até a gravação no H2, citando as classes.
```

**2. Verificação do ambiente (Passo 2)**
```text
Verifique se Java 21, Maven e Git estão instalados. Se faltar algo, me avise antes de instalar.
Rode mvn test no projeto base e mostre o resultado antes de qualquer mudança.
Faça um commit só com o projeto base, sem alterações, numa branch aluno/f3rreiraa.
```

**3. Prioridade – análise (Passo 7)**
```text
Leia AGENTS.md e use backend-java, frontend e testing quando aplicável.
Novo requisito: toda tarefa deve possuir prioridade LOW, MEDIUM ou HIGH.
Não implemente ainda. Analise impactos em entidade, DTOs, serviço, API,
frontend, validação e testes. Apresente um plano incremental e os arquivos afetados.
```

**4. Prioridade – implementação**
```text
Implemente o plano em pequenas etapas. Preserve a arquitetura Controller -> Service -> Repository.
Prioridade deve ser obrigatória (400 se não vier). Mantenha o estilo de código que já existe.
Ao final de cada etapa, compile ou execute os testes relevantes.
Não adicione frameworks nem dependências desnecessárias.
```

**5. Testes (Passo 8)**
```text
Use a Skill testing. Revise TaskServiceTest e crie testes para a prioridade
(criar, atualizar, buscar por id e atualizar tarefa inexistente).
Use Arrange, Act, Assert. Não enfraqueça asserções existentes, só adapte os construtores.
Execute mvn test e explique eventuais falhas.
```

**6. Depuração controlada (Passo 9)** — eu troquei `/api/tasks` por `/api/task` no `app.js` e mandei:
```text
A listagem deixou de funcionar. Não altere nada ainda.
Investigue frontend e backend, identifique causa raiz, apresente evidência
(requisições de rede, status HTTP, resposta da API) e proponha a menor correção.
Aguarde minha autorização para editar.
```
Depois: `Pode aplicar a correção. Não faça commit do erro proposital, confirme que o git status ficou limpo.`

**7. Desafio final – análise e implementação**
```text
Use backend-java, frontend e testing.
Requisito: GET /api/tasks?title=copilot&status=PENDING. Os filtros são opcionais e podem ser usados
separados ou juntos. A busca por título ignora maiúsculas/minúsculas. Os filtros devem aparecer no frontend.
Nenhum Controller pode acessar Repository. Não implemente ainda: proponha o plano e diga
como vai tratar título vazio e status inválido.
```
```text
Plano aprovado. Implemente por etapas (repository, service, controller, frontend, testes).
Os testes devem cobrir: sem filtro, só título, só status, os dois juntos e título em branco.
Rode mvn test e depois teste na aplicação rodando com curl e no navegador.
```

**8. Code review (Passo 10)**
```text
Use a Skill code-review. Analise somente o git diff entre o commit base e o HEAD e não edite arquivos.
Classifique achados em CRITICAL, HIGH, MEDIUM, LOW ou SUGGESTION.
Para cada achado, mostre arquivo, evidência, impacto e correção recomendada.
Verifique aderência ao AGENTS.md e testes faltantes.
```
Depois: `Corrija os achados MEDIUM e LOW e a sugestão de testes do controller. Rode mvn test.`

## Plano proposto pelo Copilot

**Prioridade**
1. Criar o enum `TaskPriority` (LOW, MEDIUM, HIGH) no pacote `entity`.
2. Adicionar o campo `priority` em `Task` (`@Enumerated(STRING)`, `nullable = false`), no construtor e no `update`.
3. Adicionar `priority` com `@NotNull` em `TaskRequest` e em `TaskResponse`.
4. Ajustar `TaskService` (create, update e `toResponse`).
5. Frontend: `<select>` de prioridade no formulário (padrão MEDIUM), mandar no payload, preencher ao editar e mostrar como badge colorido na lista.
6. Ajustar os testes antigos (construtor mudou) e criar os novos.
7. Observação do Copilot: como o banco é H2 em memória com `create-drop`, não precisa de migração pros dados antigos.

**Busca/filtro**
1. `TaskRepository`: três *derived queries* do Spring Data: `findByTitleContainingIgnoreCase`, `findByStatus` e `findByTitleContainingIgnoreCaseAndStatus`.
2. `TaskService.findAll(title, status)`: escolhe a consulta conforme os filtros. Título em branco = sem filtro, e faz `trim()`.
3. `TaskController`: `@RequestParam(required = false)` pros dois parâmetros, só repassando pro service.
4. Status inválido (`?status=XYZ`) deve dar 400 em JSON.
5. Frontend: campo de busca + select de status + botões Filtrar/Limpar, montando a query com `URLSearchParams`.
6. Testes no service (Mockito) e um `@DataJpaTest` pra garantir que o ignore case funciona de verdade no H2.

## Alterações aceitas e rejeitadas

**Aceitas**
- Enum `TaskPriority` separado e prioridade obrigatória (`@NotNull`).
- *Derived queries* no repository em vez de escrever JPQL na mão: menos código e o Spring gera o `LOWER(...) LIKE`.
- `@DataJpaTest` pro repository: eu não tinha pensado nisso, mas faz sentido, porque o teste com Mockito não prova que o ignore case funciona no banco.
- Mover a chamada inicial `load()` pro final do `app.js`. Na hora de colocar os filtros, o Copilot percebeu que o `load()` rodava antes das variáveis `filterTitle`/`filterStatus` serem declaradas (erro de TDZ com `const`), então a lista ia quebrar ao abrir a página.
- Handler genérico de erro que reaproveita o status das exceções do próprio Spring (`ErrorResponse`), pra rota inexistente continuar 404 e método errado continuar 405, em vez de virar tudo 500.
- Mensagens de validação em português.
- Renomear o projeto de `task-manager-copilot` para `gerenciador-tarefas` (no `pom.xml`, no `application.properties` e no título da página). O nome antigo era só o nome do laboratório e não dizia o que o sistema faz. O pacote `br.edu.taskmanager` continuou igual pra não mexer em todos os arquivos à toa.

**Rejeitadas / não feitas**
- Usar `Specification`/Criteria API pro filtro: seria bom se tivesse muitos filtros, mas pra dois é complicar à toa.
- Trocar o `GlobalExceptionHandler` por `ResponseEntityExceptionHandler` com `ProblemDetail`: mudaria o formato do JSON de erro que o front já usa (`message`, `errors`).
- Busca automática enquanto digita (com debounce): deixei com botão "Filtrar"/Enter pra ficar mais simples. O select de status filtra direto ao mudar.
- Paginação na listagem: fora do escopo da atividade.

## Evidências de testes

`mvn test` no final: **BUILD SUCCESS, 28 testes, 0 falhas**

| Classe | Testes | O que cobre |
|---|---:|---|
| `TaskServiceTest` | 14 | os 4 originais + prioridade (criar, atualizar, buscar, atualizar inexistente) + excluir inexistente + filtro (sem filtro, só título, só status, os dois, título em branco/com espaço) |
| `TaskRepositoryTest` (`@DataJpaTest`) | 3 | busca ignore case no H2 de verdade, por status e combinada |
| `TaskControllerTest` (`@WebMvcTest`) | 11 | repasse dos filtros, 200 buscar por id, 201 + Location, 200 atualizar, 204 excluir, 404 excluir inexistente, 400 título curto, 400 sem prioridade, 400 prioridade inexistente, 400 status inválido, 404 buscar inexistente |

Teste manual com a aplicação rodando (`mvn spring-boot:run`):
```text
GET  /api/tasks?title=copilot&status=PENDING   -> 200, só "Estudar Copilot"
GET  /api/tasks?title=COPILOT                  -> 200, as duas que têm "copilot" no título
GET  /api/tasks?status=PENDING                 -> 200, só as pendentes
GET  /api/tasks?status=XYZ                     -> 400 {"message":"Valor inválido para o parâmetro 'status'"}
POST /api/tasks {"title":"ab",...}             -> 400 {"errors":{"title":"Título deve ter entre 3 e 100 caracteres"}}
POST /api/tasks {...,"priority":"URGENTE"}     -> 400 com mensagem explicando os valores aceitos
GET  /api/tasks/99                             -> 404 {"message":"Tarefa não encontrada: 99"}
```
No navegador, digitar "COPILOT" na busca e escolher PENDING gerou `GET /api/tasks?title=COPILOT&status=PENDING` e mostrou só a tarefa certa.

Uma coisa que aconteceu: quando testei pelo curl no Git Bash do Windows, as tarefas com acento ("laboratório", "Cálculo") voltavam 400. Não era bug da API: o terminal mandava o texto em outra codificação e o JSON chegava inválido. Mandando o JSON de um arquivo UTF-8 (`--data-binary @arquivo.json`) funcionou normal.

## Erro investigado e causa raiz

**Sintoma:** depois de trocar `/api/tasks` por `/api/task` no `app.js`, a lista ficou vazia e apareceu só "Falha na operação" na tela.

**Evidência:**
- Na aba de rede do navegador: `GET http://localhost:8080/api/task → 404`.
- `curl -i localhost:8080/api/task` retornou `404` com `{"status":404,"error":"Not Found","path":"/api/task"}`.
- O `TaskController` está mapeado com `@RequestMapping("/api/tasks")`, então `/api/task` não existe.

**Causa raiz:** URL base errada na constante `api` do frontend. O backend estava certo. A mensagem era genérica porque a resposta padrão do Spring pra 404 não tem o campo `message` que o `app.js` lê, então caía no texto padrão `'Falha na operação'`.

**Correção:** voltar a constante pra `'/api/tasks'` (uma linha). O `git status` ficou limpo e o erro proposital não foi commitado.

**Detalhe que me pegou:** depois de corrigir o arquivo, continuou dando 404. Era porque o `spring-boot:run` serve os arquivos estáticos copiados em `target/classes`, e não direto de `src/`. Precisou reiniciar a aplicação pra pegar o `app.js` corrigido.

## Resultado do code review

Review do diff `a18e48a..HEAD` (projeto base até o desafio pronto):

| Severidade | Arquivo | Achado | Status |
|---|---|---|---|
| MEDIUM | `GlobalExceptionHandler.java` | JSON malformado, enum inválido no body (`"priority":"URGENTE"`) e `?status=XYZ` caíam no erro padrão do Spring, sem `message` e com formato diferente do `ApiError`. O front mostrava só "Falha na operação". | Corrigido: handlers pra `HttpMessageNotReadableException` e `MethodArgumentTypeMismatchException` + handler genérico sem stack trace |
| LOW | `TaskRequest.java` | Mensagens de validação em inglês ("size must be between 3 and 100") aparecendo pro usuário. | Corrigido: `message` em português nas anotações |
| LOW | `app.js` | Erro de validação mostrava só "Dados inválidos", sem dizer qual campo. | Corrigido: o front junta os textos de `errors` na mensagem |
| SUGGESTION | testes | Não tinha nenhum teste da camada Controller (status HTTP, query params, validação). | Corrigido: `TaskControllerTest` com 11 testes |
| OK | arquitetura | Controller só chama o Service; Repository só é usado no Service. Injeção por construtor mantida. Nenhuma dependência nova no `pom.xml`. Nenhum segredo no código. | — |

## Aprendizados
- Pedir **"não implemente ainda, faça um plano"** antes faz muita diferença: deu pra discutir coisas como título em branco e status inválido antes de ter código pra jogar fora.
- O `AGENTS.md` e as Skills funcionam como um "contrato": o Copilot respeitou a arquitetura e não adicionou dependência sem pedir, porque estava escrito lá.
- Não dá pra confiar só no "os testes passaram": o teste do service com Mockito passava mesmo que o ignore case não funcionasse no banco. Por isso o `@DataJpaTest`.
- Na depuração, separar **sintoma** (lista vazia), **evidência** (404 na aba Network) e **causa raiz** (URL errada) deixa a correção bem menor e mais segura.
- Revisar o `git diff` antes do commit e fazer commits pequenos por etapa deixa o histórico bem mais fácil de entender.
- Ainda assim, a responsabilidade é minha: eu tive que entender cada mudança (por exemplo o problema do `const` no JS e do `target/classes`) pra conseguir explicar.
