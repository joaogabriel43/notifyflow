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
(seção vazia no início — será preenchida conforme o projeto evolui)

## 🚀 Otimizações e Performance
(seção vazia no início)

## 🤖 Agentes: Casos de Uso Confirmados

| Agente | Tarefa | Sprint |
|--------|--------|--------|
| @engineering-devops-automator | Setup Git e estrutura Maven | 1 |
| @engineering-backend-architect | Modelagem do domínio | 1 |
| @engineering-database-optimizer | Migrations Flyway | 1 |
| @engineering-technical-writer | Criação do CLAUDE.md | 1 |
| @testing-api-tester | Testes unitários de domínio | 1 |

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
