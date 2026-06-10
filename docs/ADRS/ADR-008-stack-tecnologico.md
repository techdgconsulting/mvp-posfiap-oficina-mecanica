# ADR-008: Stack Tecnológico do Projeto

**Status:** Aceito  
**Data:** 2026-05-22  
**Autor:** Diego Gonzalez  
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O projeto exige uma plataforma backend consolidada, com suporte a DDD tático, persistência relacional, segurança, documentação de API e cobertura de testes. As escolhas de linguagem, framework e bibliotecas de suporte precisam ser registradas e justificadas para garantir rastreabilidade das decisões.

> As decisões de PostgreSQL, JWT, Flyway, Docker e Gateway de Pagamento possuem ADRs dedicadas (ADR-003 a ADR-007). Esta ADR cobre o restante do stack não documentado anteriormente.

---

## Decisões

### 1. Linguagem: Java 17 (LTS)

**Decisão:** Java 17, versão LTS (Long-Term Support) da plataforma.

| Alternativa | Motivo da rejeição |
|-------------|-------------------|
| Java 21 (LTS) | Versão mais recente mas com suporte ainda em consolidação no ecossistema Spring Boot 3.x na época da decisão |
| Kotlin | Curva de aprendizado adicional; Java é a linguagem base do curso FIAP |
| Java 11 | LTS anterior; sem records, sealed classes e melhorias de switch |

**Consequências:** Records do Java 14+ usados para DTOs imutáveis (`CriarOrdemServicoRequest`, `ClienteRequest`, etc.). `var` reduz verbosidade. Suporte garantido até 2029.

---

### 2. Framework: Spring Boot 3.3.5

**Decisão:** Spring Boot 3.3.5 como framework principal.

**Justificativas:**
- Autoconfiguração elimina XML e reduz boilerplate de infraestrutura
- Integração nativa com Spring Data JPA, Spring Security e Spring Web MVC
- Versão 3.x requer Java 17+ (alinhado com a decisão de Java 17)
- Ecossistema maduro, amplamente adotado na indústria

| Alternativa | Motivo da rejeição |
|-------------|-------------------|
| Quarkus | Curva de aprendizado; menor adoção na academia |
| Micronaut | Idem |
| Spring Boot 2.x | Suporte encerrado; não compatível com Jakarta EE 9+ |

---

### 3. Build Tool: Maven

**Decisão:** Apache Maven com herança de `spring-boot-starter-parent`.

**Justificativas:**
- Gerenciamento de versões de dependências via BOM do Spring Boot (evita conflitos)
- Ampla adoção e documentação consolidada
- Integração nativa com IDEs e CI/CD

| Alternativa | Motivo da rejeição |
|-------------|-------------------|
| Gradle | Sintaxe mais flexível mas com maior complexidade de configuração para iniciantes |

---

### 4. Redução de Boilerplate: Lombok

**Decisão:** Project Lombok para geração de código em tempo de compilação.

**Anotações utilizadas:**

| Anotação | Uso |
|----------|-----|
| `@Getter` | Gera getters para todos os campos |
| `@Builder` | Padrão Builder para construção de objetos |
| `@NoArgsConstructor(access = PROTECTED)` | Construtor protegido exigido pelo JPA |
| `@AllArgsConstructor` | Construtor com todos os campos (Builder) |
| `@RequiredArgsConstructor` | Injeção de dependências via construtor nos Services |
| `@Slf4j` | Logger SLF4J injetado automaticamente |
| `@EqualsAndHashCode` | Value Objects (CpfCnpj, Placa, Quantidade) |

**Consequências:** Código de domínio mais limpo e focado em regras de negócio. Lombok é excluído do artefato final (`<optional>true</optional>`).

---

### 5. Documentação de API: SpringDoc OpenAPI 2.6.0

**Decisão:** `springdoc-openapi-starter-webmvc-ui` versão 2.6.0 para geração automática do contrato OpenAPI 3.0 e interface Swagger UI.

**Acesso em desenvolvimento:** `http://localhost:8080/swagger-ui.html`

**Justificativas:**
- Geração automática a partir das anotações `@RestController` e `@RequestMapping`
- `@Operation` e `@Tag` enriquecem a documentação sem overhead de manutenção
- Compatível com Spring Boot 3.x (springdoc v2 exigido — v1 não é compatível)

| Alternativa | Motivo da rejeição |
|-------------|-------------------|
| Springfox | Descoontinuado; incompatível com Spring Boot 3 |
| Documentação manual (Postman/YAML) | Existe como complemento (`postman/`), não como substituto |

---

### 6. Biblioteca JWT: JJWT 0.12.6

**Decisão:** `io.jsonwebtoken:jjwt-api/impl/jackson` versão 0.12.6 como implementação da geração e validação de tokens JWT.

> A estratégia JWT (stateless, HMAC-SHA256) está documentada em **ADR-004**.

**Justificativas:**
- API fluente e tipada para criação de claims e parsing
- Suporte a HMAC-SHA256 (`HS256`) com chave segura de 256 bits
- Versão 0.12.x reescrita com API simplificada em relação à 0.11.x

---

### 7. Cobertura de Testes: JaCoCo 0.8.12

**Decisão:** JaCoCo Maven Plugin versão 0.8.12 para coleta e relatório de cobertura de código.

**Configuração:**
- Relatório gerado em `target/site/jacoco/` a cada `mvn test`
- Exclusões configuradas: `*JpaRepository*` (adaptadores JPA gerados) e `OficinaMecanicaDGCARApplication` (ponto de entrada)

**Justificativas:**
- Integrado ao ciclo `test` do Maven sem configuração adicional
- Relatório HTML navegável por classe e linha
- Padrão de mercado para projetos Java/Maven

---

### 8. Testes de Integração: Testcontainers

**Decisão:** `org.testcontainers:junit-jupiter` + `org.testcontainers:postgresql` para testes de integração com PostgreSQL real em container Docker.

**Justificativas:**
- Elimina dependência de banco de dados externo nos testes
- PostgreSQL real no teste garante fidelidade com o ambiente de produção (vs. H2 com dialeto diferente)
- Integração nativa com JUnit 5 via `@Testcontainers` + `@Container`

| Alternativa | Motivo da rejeição |
|-------------|-------------------|
| H2 em modo compatibilidade | Dialeto PostgreSQL incompleto; comportamentos divergentes em constraints e tipos |
| Banco externo fixo | Cria dependência de infraestrutura externa; não reprodutível em CI |

---

### 9. Relatórios de Teste: Allure 2.27.0

**Decisão:** `io.qameta.allure:allure-junit5` versão 2.27.0 + `allure-maven` plugin 2.12.0 para geração de relatórios de testes enriquecidos.

**Justificativas:**
- Relatórios HTML navegáveis com agrupamento por `@Epic`, `@Feature` e `@Story`
- Integração nativa com JUnit 5 via anotações declarativas nos testes
- Geração automática junto ao ciclo `mvn test` via maven-surefire-plugin

---

### 10. Banco In-Memory para Dev Local: H2

**Decisão:** H2 Database em `scope runtime` como banco alternativo para execução local sem Docker.

**Escopo:** Apenas desenvolvimento local (perfil padrão sem Docker). Testes de integração usam Testcontainers com PostgreSQL real.

---

## Resumo do Stack

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Linguagem | Java | 17 (LTS) |
| Framework | Spring Boot | 3.3.5 |
| Build | Maven | (spring-boot-starter-parent) |
| Web | Spring Web MVC | (managed) |
| Persistência | Spring Data JPA / Hibernate | (managed) |
| Segurança | Spring Security | (managed) |
| JWT | JJWT | 0.12.6 |
| Banco Produção | PostgreSQL | 16 (ADR-003) |
| Banco Dev | H2 | (managed) |
| Migrations | Flyway | (managed + ADR-005) |
| Boilerplate | Lombok | (managed) |
| Documentação API | SpringDoc OpenAPI | 2.6.0 |
| Cobertura | JaCoCo | 0.8.12 |
| Relatórios de Teste | Allure | 2.27.0 |
| Testes Integração | Testcontainers | (managed) |
| Containerização | Docker / Docker Compose | (ADR-006) |
