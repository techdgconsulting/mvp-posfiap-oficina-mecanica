# ADR-009: Estratégia de Testes — Frameworks e Ferramentas

**Status:** Aceito  
**Data:** 2026-05-22  
**Autor:** Diego Gonzalez  
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

---

## Contexto

O projeto precisa de uma estratégia de testes abrangente que valide as regras de negócio do domínio (DDD), a orquestração dos serviços de aplicação, a segurança (RBAC/JWT) e os endpoints REST, de forma independente do banco de dados de produção (PostgreSQL).

O desafio é selecionar ferramentas que:
1. Cubram os três níveis de teste: unitário puro, serviço com mocks e controller com MockMvc
2. Sejam compatíveis com Java 17, Spring Boot 3.5.16 e Spring Security gerenciado pelo BOM do Spring Boot
3. Permitam isolamento real do banco em CI/CD
4. Gerem relatório de cobertura automaticamente no build Maven

---

## Decisão

Adoção da **pirâmide de testes** com três camadas distintas, usando o stack a seguir.

### Evidência local atual

| Métrica | Valor | Fonte |
|---|---:|---|
| Classes de teste | 39 | `src/test/**/*.java` |
| Testes executados | 358 | última execução local de `mvn test` |
| Cobertura de instruções | 95,09% | `target/site/jacoco/jacoco.csv` |

---

## Stack de Testes — Versões Resolvidas

> Todas as versões abaixo são as versões **resolvidas em tempo de build** (`mvn dependency:list`).  
> Dependências sem versão explícita no `pom.xml` são gerenciadas pelo BOM do `spring-boot-starter-parent 3.5.16`.

### Núcleo de Testes

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `org.junit.jupiter:junit-jupiter` | **5.10.5** | Framework de testes JUnit 5 — aggregator (api + engine) |
| `org.junit.jupiter:junit-jupiter-api` | **5.10.5** | API de anotações: `@Test`, `@BeforeEach`, `@Nested`, `@DisplayName` |
| `org.junit.jupiter:junit-jupiter-engine` | **5.10.5** | Motor de execução JUnit 5 |
| `org.junit.jupiter:junit-jupiter-params` | **5.10.5** | Suporte a `@ParameterizedTest` com `@ValueSource`, `@CsvSource`, `@MethodSource` |
| `org.junit.platform:junit-platform-commons` | **1.10.5** | Utilitários da plataforma JUnit |
| `org.junit.platform:junit-platform-engine` | **1.10.5** | SPI da plataforma de execução |
| `junit:junit` | **4.13.2** | JUnit 4 — apenas transitivo (exigido por algumas dependências internas do Spring) |
| `org.opentest4j:opentest4j` | **1.3.0** | API de asserções abertas usada pelo JUnit 5 internamente |

### Mocking e Asserções

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `org.mockito:mockito-core` | **5.11.0** | Framework de mocks — `@Mock`, `@Spy`, `when().thenReturn()`, `verify()` |
| `org.mockito:mockito-junit-jupiter` | **5.11.0** | Integração Mockito ↔ JUnit 5 — habilita `@ExtendWith(MockitoExtension.class)` |
| `org.assertj:assertj-core` | **3.25.3** | API fluente de asserções: `assertThat(valor).isEqualTo(...)`, `assertThatThrownBy(...)` |
| `org.hamcrest:hamcrest` | **2.2** | Matchers para asserções no estilo `assertThat(x, is(y))` (usado pelo MockMvc) |
| `org.hamcrest:hamcrest-core` | **2.2** | Módulo core do Hamcrest (transitivo deprecado, mantido por compatibilidade) |

### Spring Boot Test

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `org.springframework.boot:spring-boot-starter-test` | **3.5.16** | BOM de testes: agrega JUnit 5, Mockito, AssertJ, Hamcrest, MockMvc, JsonPath, etc. |
| `org.springframework.security:spring-security-test` | **managed** | Suporte a `@WithMockUser`, `SecurityMockMvcRequestPostProcessors.csrf()`, `@WithUserDetails` |

### Banco de Dados para Testes

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `com.h2database:h2` | **2.2.224** | Banco em memória (perfil `test`) — substitui PostgreSQL nos testes unitários e de integração leve |

### Testcontainers (integração com banco real)

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `org.testcontainers:junit-jupiter` | **managed** | Integração Testcontainers ↔ JUnit 5 (`@Testcontainers`, `@Container`) |
| `org.testcontainers:postgresql` | **managed** | Container PostgreSQL para testes de integração com banco real |
| `org.testcontainers:testcontainers` | **managed** | Núcleo do Testcontainers |
| `org.testcontainers:database-commons` | **managed** | Utilitários comuns para containers de banco de dados |
| `org.testcontainers:jdbc` | **managed** | Suporte ao protocolo JDBC sobre containers |

> **Nota:** Testcontainers está declarado no `pom.xml` como dependência de teste, mas **não é utilizado nos testes atuais** (os testes usam H2). Está disponível como ponto de extensão para testes de integração com PostgreSQL real em pipelines CI.

### Cobertura de Código

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `org.jacoco:jacoco-maven-plugin` | **0.8.12** | Plugin Maven para instrumentação em tempo de execução e geração de relatório HTML/XML |

### Relatório de Testes — Allure

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `io.qameta.allure:allure-junit5` | **2.27.0** | Listener JUnit 5 que intercepta cada `@Test` e grava resultado em `target/allure-results/` |
| `io.qameta.allure:allure-maven` | **2.12.0** | Plugin Maven (`mvn allure:report`) que converte `allure-results/` em HTML interativo |

### Plugin de Execução

| Artefato | Versão | Papel no projeto |
|---|---|---|
| `maven-surefire-plugin` | **3.2.5** | Plugin Maven responsável por descobrir e executar os testes JUnit 5 |

---

## Arquitetura de Testes — Três Camadas

```
┌─────────────────────────────────────────────────────────────────┐
│  CAMADA 3 — Controller Tests (@WebMvcTest + MockMvc)            │
│  Anota: @WebMvcTest, @MockitoBean, @Import(SecurityConfig.class)│
│  Simula: HTTP, usuário autenticado, RBAC e respostas JSON       │
│  Ferramentas: Spring MockMvc, @WithMockUser, spring-security-test│
├─────────────────────────────────────────────────────────────────┤
│  CAMADA 2 — Use Case Tests (@ExtendWith(MockitoExtension))      │
│  Anota: @Mock; eventualmente @InjectMocks e @Captor             │
│  Simula: repositórios, gateways, clientes HTTP e ports de saída │
│  Ferramentas: Mockito 5.11.0, AssertJ 3.25.3                   │
├─────────────────────────────────────────────────────────────────┤
│  CAMADA 1 — Domain Tests (JUnit puro, sem Spring)               │
│  Anota: @Test, @ParameterizedTest, @ValueSource                 │
│  Valida: entidades, VOs, estados, invariantes e cálculos        │
│  Ferramentas: JUnit Jupiter 5.10.5, AssertJ 3.25.3             │
└─────────────────────────────────────────────────────────────────┘
```

### Mapeamento por camada de código

| Pacote testado | Camada de teste | Anotação principal | Banco |
|---|---|---|---|
| `domain.model.*` / `domain.valueobject.*` | Unitário puro | `@Test` / `@ParameterizedTest` | Nenhum |
| `application.usecase.*` | Use case com mocks | `@ExtendWith(MockitoExtension.class)` | Nenhum |
| `adapters.in.web.controller.*` | Controller (web slice) | `@WebMvcTest` + `MockMvc` | Nenhum |
| `adapters.out.persistence.*` | Adapter de persistência | Testes focados por adapter | H2 quando necessário |
| `adapters.out.payment.*` | Gateway mock | `@Test` | Nenhum |
| `adapters.out.security.*` / `infrastructure.security.*` | Segurança/JWT | Unitário e slice web | Nenhum |
| `infrastructure.client.*` | Cliente externo (ViaCEP) | Unitário com mock HTTP | Nenhum |
| `coverage.*` | Cobertura de cenários residuais | JUnit 5 | Conforme classe |

---

## Configuração de Segurança nos Testes de Controller

O `SecurityConfig` está no pacote `infrastructure`, fora do escopo de auto-scan do `@WebMvcTest`. A solução adotada:

```java
@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)           // Carrega manualmente a config de segurança
class ClienteControllerTest {

    @MockitoBean TokenProviderPort tokenProviderPort; // Evita falha ao construir o filtro JWT
    @MockitoBean UserDetailsService userDetailsService; // Idem para o UserDetailsService

    @WithMockUser(roles = "GESTOR")     // Simula usuário autenticado com perfil GESTOR
    @Test
    void deveCriarCliente() { ... }

    @Test  // sem @WithMockUser → request não autenticado → espera 401
    void semAutenticacao_retorna401() { ... }
}
```

---

## Perfil de Teste (`application.yml`, profile `test`)

```yaml
spring:
  config:
    activate:
      on-profile: test
  datasource:
    url: jdbc:h2:mem:oficina-test
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```

No estado atual, os testes automatizados priorizam velocidade e isolamento: o schema H2 é recriado pelo Hibernate e o Flyway permanece desabilitado no perfil `test`. As migrations Flyway são validadas no ambiente PostgreSQL principal/Docker, conforme ADR-005.

---

## Configuração Allure Report

Os testes principais estão anotados com metadados do Allure:

```java
@Epic("Ordem de Serviço")          // Agrupamento de alto nível (domínio de negócio)
@Feature("API REST Ordens de Serviço") // Funcionalidade dentro do Epic
class OrdemDeServicoControllerTest {

    @Test
    @Story("Criar OS via API")       // Caso de uso específico
    void deveCriarOS() { ... }
}
```

**Mapeamento Epic → Pacote:**

| Epic | Pacotes cobertos |
|---|---|
| Atendimento ao Cliente | `domain/model/**`, `domain/valueobject/**`, `application/usecase/*Cliente*`, `application/usecase/*Veiculo*`, `adapters/in/web/controller/*Cliente*`, `adapters/in/web/controller/*Veiculo*` |
| Ordem de Serviço | `domain/model/OrdemDeServico`, `application/usecase/*Ordem*`, `adapters/in/web/controller/OrdemDeServicoController` |
| Execução Técnica | `domain/model/Diagnostico`, `domain/model/Execucao`, `domain/model/Entrega`, `domain/model/Encerramento` |
| Gestão de Estoque | `domain/model/Peca`, `application/usecase/*Peca*`, `adapters/in/web/controller/PecaController` |
| Catálogo de Serviços | `domain/model/Servico`, `application/usecase/*Servico*`, `adapters/in/web/controller/ServicoController` |
| Faturamento | `domain/model/Pagamento`, `domain/model/Orcamento`, `adapters/out/payment/**` |
| Segurança e Autenticação | `infrastructure/security/**`, `adapters/out/security/**`, `adapters/in/web/controller/AuthController` |
| Integrações Externas | `adapters/out/payment/**`, `infrastructure/client/**`, `adapters/out/viacep/**` |
| Aplicação | `application/usecase/**`, `coverage/**` |

**Configuração `maven-surefire-plugin`** (argLine JaCoCo):

```xml
<argLine>${argLine}</argLine>
<useSystemClassLoader>false</useSystemClassLoader>
<systemPropertyVariables>
  <allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
</systemPropertyVariables>
```

> **Nota:** `aspectjweaver` **não é necessário** para `@Epic`/`@Feature`/`@Story` — essas anotações
> funcionam por reflexão Java via `AllureJunit5` (JUnit Platform `TestExecutionListener`).
> O AspectJ só seria necessário para `@Step` em métodos auxiliares não-teste (não usados aqui).

**Geração do relatório — Windows (PowerShell):**
```powershell
.\allure-report.ps1            # roda testes + gera + abre no browser
.\allure-report.ps1 -SkipTests # gera sem rodar testes (reutiliza allure-results)
```
Relatório gerado em: `target/allure-report/index.html`

> **Por que não `mvn test allure:report`?**  
> O plugin `allure-maven` executa o Allure CLI via `allure.bat` no Windows.  
> O `.bat` não trata corretamente o `&` em caminhos (ex: `D&G Consulting`), truncando o
> `CLASSPATH` e causando `ClassNotFoundException: io.qameta.allure.CommandLine`.  
> O script `allure-report.ps1` contorna isso invocando o `java -cp` diretamente no PowerShell.

---

## Configuração JaCoCo

O endpoint `POST /api/ordens-servico/completa` possui cobertura específica em teste de controller (`OrdemDeServicoControllerTest`) e teste de caso de uso (`CriarOrdemServicoCompletaUseCaseTest`), validando o contrato HTTP e as regras de reaproveitamento/criação de cliente e veículo.

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <configuration>
    <excludes>
      <exclude>**/OficinaMecanicaDGCARApplication.class</exclude>
    </excludes>
  </configuration>
  <executions>
    <execution><goals><goal>prepare-agent</goal></goals></execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals><goal>report</goal></goals>
    </execution>
  </executions>
</plugin>
```

Relatório gerado em: `target/site/jacoco/index.html`

---

## Alternativas Consideradas

| Opção | Motivo da escolha / rejeição |
|---|---|
| **Testcontainers com PostgreSQL real** | Disponível no `pom.xml`; preferiu-se H2 nos testes atuais por velocidade. Extensão natural para CI/CD com banco real |
| **Arquillian** | Overengineering para o escopo do projeto |
| **REST Assured** | `MockMvc` (embutido no Spring) cobre todos os cenários necessários sem dependência adicional |
| **WireMock (mocks HTTP externos)** | Mockito + `@MockitoBean` suficientes para mockar ports e adapters como `TokenProviderPort`, `ViaCepClient` e `MockPagamentoGatewayAdapter` |
| **Banco PostgreSQL embutido (pg-embedded)** | H2 em memória oferece startup simples para a suíte atual; PostgreSQL real fica concentrado no ambiente Docker e em futura evolução com Testcontainers |
| **Allure vs. Surefire HTML puro** | Surefire gera relatório básico; Allure oferece agrupamento por Epic/Feature/Story, gráficos de tendência, histórico de execuções e drill-down por teste — justifica a dependência extra |

---

## Consequências

### Positivas
- **Velocidade:** testes de domínio e use cases em < 100 ms por classe (sem I/O)
- **Isolamento:** cada camada testada sem dependências das camadas superiores
- **Cobertura alta:** 95,09% de instruções na evidência local atual — detecta regressões com confiança
- **RBAC validado:** testes de negação (403) em todas as regras de perfil dos controllers
- **Relatório de cobertura:** JaCoCo gera HTML a cada `mvn test jacoco:report`
- **Relatório de testes:** Allure gera dashboard interativo via `allure-report.ps1` (Windows) com agrupamento por Epic/Feature/Story, histórico e tendências

### Negativas
- H2 em memória não cobre 100% das features específicas do PostgreSQL (ex: dialeto, constraints específicas, funções nativas)
- Testes de controller (`@WebMvcTest`) exigem `@Import(SecurityConfig.class)` manual — acoplamento frágil que pode ser esquecido ao criar novos tests
- Testcontainers declarados no `pom.xml` mas não utilizados nos testes atuais — pode gerar confusão sobre qual banco usar em testes de integração futuros
