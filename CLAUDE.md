# CLAUDE.md — NotifyFlow
> Versão: 1.0.0 | Última atualização: 2025-07-16

## 🏗️ Visão Geral do Projeto
**NotifyFlow** é um motor de notificações multi-canal assíncrono com garantia de entrega.

**Problema que resolve**: Centralizar e garantir entrega de notificações com fallback automático
entre canais (EMAIL → SMS → PUSH), eliminando falhas silenciosas comuns em sistemas legados.

**Stack**:
- Java 17 + Spring Boot 3.2
- RabbitMQ (mensageria assíncrona)
- PostgreSQL 16 + Flyway (migrations)
- Resilience4j (Circuit Breaker + Retry + Rate Limiter)
- Thymeleaf (template engine)
- Testcontainers (testes de integração)
- Angular 17 (dashboard — Sprint 5)

**Integrações externas**:
- SendGrid (email — Sprint 4)
- Twilio (SMS — mockado, Sprint 4)
- Firebase Cloud Messaging (push — mockado, Sprint 4)

## 📁 Estrutura de Pastas
```
src/main/java/com/joaogabriel/notifyflow/
├── domain/
│   ├── model/          # Entidades de domínio puras (sem anotações JPA)
│   ├── enums/          # Enums de domínio
│   ├── exception/      # Exceções de domínio
│   └── port/out/       # Interfaces de saída (portas do hexágono)
├── application/
│   ├── usecase/        # Interfaces dos casos de uso
│   ├── service/        # Implementações dos casos de uso
│   └── dto/            # DTOs de entrada e saída
├── infrastructure/
│   ├── persistence/    # JPA entities, repositories, adapters, mappers
│   ├── messaging/      # RabbitMQ publisher, consumer, config
│   ├── channel/        # Senders por canal (Email, SMS, Push)
│   └── config/         # Beans de configuração Spring
└── presentation/
    ├── controller/     # REST Controllers
    └── exception/      # GlobalExceptionHandler
```

## ⚙️ Configurações do Ambiente

- Java 17 obrigatório
- PostgreSQL 16 local ou via Docker
- RabbitMQ local ou via Docker
- Variáveis de ambiente: ver `.env.example` na raiz
- Para rodar localmente: `docker-compose up -d` (quando criado no Sprint 3)

## 📐 Padrões do Projeto

### DTOs na fronteira
Toda comunicação entre Controller ↔ Service usa DTOs.
O domínio (`Notification`, `DeliveryAttempt`) nunca sai da camada de aplicação.
Mapeamento via MapStruct: `NotificationMapper.java`

### Injeção de dependência via construtor
Nunca usar `@Autowired` em campo. Sempre construtor + `final`.

### Nomenclatura
- **Ports** (interfaces de domínio): sufixo `Port` — ex: `NotificationRepositoryPort`
- **Adapters** (infra implementando ports): sufixo `Adapter` — ex: `NotificationRepositoryAdapter`
- **Use Cases** (interfaces): sufixo `UseCase` — ex: `SendNotificationUseCase`
- **Services** (implementações): sufixo `Service` — ex: `SendNotificationService`
- **JPA Repositories**: sufixo `JpaRepository` — ex: `NotificationJpaRepository`

### Tratamento de erro
`GlobalExceptionHandler` centraliza todos os erros.
Retornar sempre `ProblemDetail` (RFC 7807) para consistência.

## 🏛️ ADRs — Decisões de Arquitetura

### ADR-001: RabbitMQ como broker de mensageria
- **Contexto**: Necessidade de mensageria assíncrona com DLQ nativa e suporte a retry configurável.
- **Decisão**: RabbitMQ com Spring AMQP. Kafka descartado pelo overhead de setup para projeto solo.
- **Consequências**: +DLQ nativa simples, +Testcontainers fácil | -Menor throughput que Kafka
- **Status**: Aceita

### ADR-002: Outbox Pattern para consistência entre DB e fila
- **Contexto**: Gravar notificação no DB e publicar na fila na mesma operação sem transação distribuída.
- **Decisão**: Tabela `notification_outbox` gravada na mesma transação. Scheduler lê e publica.
- **Consequências**: +Consistência garantida, +Sem mensagens perdidas | -Latência mínima adicional
- **Status**: Aceita

### ADR-003: Chain of Responsibility para fallback entre canais
- **Contexto**: Fallback automático EMAIL → SMS → PUSH quando canal falha.
- **Decisão**: Domínio controla a lógica via `getNextChannel()` + `hasMoreChannels()` na entidade `Notification`.
- **Consequências**: +Lógica de fallback testável unitariamente | -Acoplamento leve ao domínio
- **Status**: Aceita

### ADR-004: Canais externos mockados nos primeiros sprints
- **Contexto**: SendGrid e Twilio requerem credenciais e configuração. Foco dos primeiros sprints é arquitetura.
- **Decisão**: `ChannelSender` implementado como stub que loga a tentativa. Integração real no Sprint 4.
- **Consequências**: +Desenvolvimento rápido e testável | -Sem validação real de entrega até Sprint 4
- **Status**: Aceita

## 🐛 Erros Conhecidos e Como Evitá-los

- **[Sprint 2] H2 mascarou comportamento de JSONB e partial indexes**
  - **O que aconteceu**: Testes de integração usaram H2 no modo PostgreSQL.
  - **Por que**: Docker não estava disponível na máquina durante o Sprint 2.
  - **Como prevenir**: Sempre usar Testcontainers com imagem real. Nunca usar H2 em novos testes.

- **[Sprint 3] Código perdido por falta de commit antes de Copy-Item**
  - **O que aconteceu**: Sprint 3 foi implementado no scratch sem commit. Copy-Item sobrescreveu com versão antiga.
  - **Por que**: Workflow de cópia scratch → F:\Projetos não garantia commit prévio
  - **Como prevenir**: Todo prompt inclui commit em F:\Projetos\NotifyFlow como critério de sucesso obrigatório.
  - **Regra adotada**: NUNCA usar scratch. Trabalhar sempre diretamente em F:\Projetos\NotifyFlow.


## 🚀 Otimizações e Performance
(seção vazia no início)

### ADR-005: Clean Architecture (Hexagonal)
- **Contexto**: Necessidade de separação de responsabilidades e testabilidade.
- **Decisão**: Camadas domain / application / infrastructure / presentation com regra de dependência invertida.
- **Status**: Aceita

### ADR-006: Testcontainers com PostgreSQL e RabbitMQ
- **Contexto**: H2 não suporta JSONB e partial indexes do PostgreSQL.
- **Decisão**: Todos os testes de integração usam Testcontainers com imagens reais.
- **Status**: Aceita

### ADR-007: SendGrid como provider de email com fallback para stub
- **Contexto**: Email real requer API key externa. Ambiente de dev deve funcionar sem credenciais.
- **Decisão**: @ConditionalOnProperty seleciona SendGridEmailSender (prod) ou EmailChannelSender stub (dev)
- **Consequências**: +Flexibilidade de ambiente | -Dois beans para o mesmo contrato
- **Status**: Aceita

### ADR-008: Rate Limiter por tenant via Resilience4j RateLimiterRegistry
- **Contexto**: Sistema multi-tenant precisa limitar abuso por tenant sem impactar outros
- **Decisão**: RateLimiter criado dinamicamente por tenantId no RateLimiterRegistry
- **Consequências**: +Isolamento por tenant, +Configurável | -Memória proporcional ao número de tenants
- **Status**: Aceita

### ADR-009: Angular Signals para estado reativo no frontend
- **Contexto**: Angular 17 introduziu Signals como alternativa ao RxJS para estado local
- **Decisão**: Signals para estado da UI (loading, selected item). RxJS apenas para HTTP calls.
- **Consequências**: +Código mais simples e legível | -Mistura de paradigmas pode confundir
- **Status**: Aceita

### ADR-010: ngx-echarts para visualização de dados
- **Contexto**: Dashboard precisa de gráficos de barras para métricas por canal
- **Decisão**: ngx-echarts (wrapper Angular do Apache ECharts)
- **Consequências**: +Rico em opções de visualização | -Dependência adicional de ~1MB
- **Status**: Aceita

### ADR-011: Multi-stage Docker build para backend e frontend
- **Contexto**: Imagens Docker menores são mais rápidas para deploy e mais seguras (menos superfície de ataque)
- **Decisão**: Multi-stage build: builder (JDK 17 / Node 20) → runtime (JRE alpine / nginx alpine)
- **Consequências**: +Imagem menor (~200MB vs ~600MB), +Sem ferramentas de build em produção | -Build mais lento na primeira execução
- **Status**: Aceita


## 🤖 Agentes: Casos de Uso Confirmados

| Agente | Tarefa | Sprint |
|--------|--------|--------|
| @engineering-devops-automator | Setup Git e estrutura Maven | 1 |
| @engineering-backend-architect | Modelagem do domínio | 1 |
| @engineering-database-optimizer | Migrations Flyway | 1 |
| @engineering-technical-writer | Criação do CLAUDE.md | 1 |
| @testing-api-tester | Testes unitários de domínio | 1 |
| @senior-developer | NotificationConsumer, Circuit Breaker | 3 |
| @backend-architect | Outbox Pattern e dispatcher | 3 |
| @senior-developer | SendGrid integration, CorsConfig | 4 |
| @backend-architect | Rate Limiter, retry endpoint | 4 |
| @frontend-developer | Dashboard Angular 17 completo | 5 |
| @devops-automator | Dockerfile multi-stage + GitHub Actions CI | 6 |
| @engineering-technical-writer | README final de portfólio | 6 |



## 📚 Regras de Negócio Relevantes

- Uma `Notification` deve ter pelo menos um canal configurado (`preferredChannel` obrigatório)
- `RecipientInfo` deve ter pelo menos um campo preenchido (email, phone ou deviceToken)
- O fallback entre canais é determinado pela lista `fallbackChannels` na ordem definida
- Uma notificação com status `EXHAUSTED` não deve ser reprocessada automaticamente
- O `tenantId` é obrigatório — o sistema é multi-tenant desde o início
- Limite padrão: 100 notificações por minuto por tenant
- Apenas notificações com status FAILED ou EXHAUSTED podem ser reprocessadas via /retry
- O reprocessamento cria novo registro no outbox — não reutiliza o registro anterior

## 🔗 Dependências e Integrações Relevantes

- **Spring AMQP**: `spring-boot-starter-amqp` — abstração sobre RabbitMQ
- **Resilience4j**: Circuit Breaker aplicado nos ChannelSenders externos
- **Flyway**: migrations versionadas, nunca alterar arquivos já aplicados
- **Testcontainers**: `rabbitmq` e `postgresql` containers para testes de integração
- **MapStruct**: geração de código em compile-time, requer `mapstruct-processor` no `annotationProcessorPaths`

## 📝 Changelog do CLAUDE.md

- **1.0.0** (Sprint 1): Documento inicial criado
- **1.1.0** (Sprint 4): ADRs 007-008, regras de negócio e erros conhecidos
- **1.2.0** (Sprint 5): ADRs 009-010, tabela de agentes atualizada, stack com Angular 17
- **1.3.0** (Sprint 6): ADR-011, README final, Dockerfiles e GitHub Actions


