# ADR-011: Notificação Informativa de Status da OS por E-mail

**Status:** Aceito  
**Data:** 2026-07-07  
**Autor:** Diego Gonzalez  
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O requisito de acompanhamento da Ordem de Serviço prevê que o cliente seja informado sobre a situação atual da OS. O projeto já possui consulta de status por API, incluindo consulta pública pelo número da OS, mas também é necessário oferecer comunicação ativa ao cliente por uma ferramenta como e-mail.

A funcionalidade deve respeitar a Clean Architecture atual, sem colocar envio SMTP dentro dos controllers ou das entidades de domínio, e sem tornar a evolução de status dependente da disponibilidade do servidor de e-mail.

## Decisão

Implementar notificação informativa por e-mail após mudanças relevantes de status da OS, usando a porta `EmailNotificacaoPort`.

O envio é acionado pelos casos de uso de OS após a transição ser validada e persistida. O e-mail informa:

- número da OS;
- status atual;
- mensagem textual amigável;
- link público para acompanhamento por número da OS.

A infraestrutura de envio passa a ter três modos configuráveis:

| Modo | Comportamento |
|------|---------------|
| `LOG` | Registra destinatário, assunto e corpo no log. É o padrão local. |
| `SMTP` | Envia e-mail real via Spring Mail / `JavaMailSender`. |
| `OFICINA_EMAIL_ENABLED=false` | Desativa notificações de forma explícita. |

## Justificativas

- Mantém a regra de negócio no caso de uso e a integração técnica em adapters de saída.
- Permite envio real por SMTP sem obrigar configuração externa em testes locais.
- Preserva a evolução de status mesmo em falhas de infraestrutura de e-mail.
- Reaproveita a mesma `EmailNotificacaoPort` já usada no fluxo de decisão externa de orçamento.
- Facilita evolução futura para AWS SES sem alterar controllers ou casos de uso consumidores.

## Consequências Positivas

- Cliente recebe comunicação ativa sobre o andamento da OS.
- O requisito é atendido sem criar novos endpoints públicos.
- A aplicação continua testável com mocks da porta de e-mail.
- O ambiente local e a suíte automatizada não dependem de servidor SMTP real.

## Consequências Negativas

- O envio é assíncrono apenas do ponto de vista de tolerância a falha; tecnicamente ainda ocorre durante o fluxo do caso de uso.
- Em modo `LOG`, não há entrega real ao cliente.
- Configuração SMTP incorreta pode gerar falhas de envio, embora não bloqueie a transição da OS.

## Alternativas Consideradas

| Alternativa | Motivo da não adoção |
|-------------|----------------------|
| Enviar e-mail diretamente no controller | Misturaria adaptação HTTP com regra de aplicação e infraestrutura |
| Usar apenas consulta pública por API | Não atende ao requisito de comunicação ativa por e-mail |
| Tornar envio obrigatório e transacional | Uma falha de SMTP poderia impedir a evolução legítima da OS |
| AWS SES direto nesta etapa | Requer configuração AWS, domínio/e-mail verificado e credenciais; permanece como evolução para implantação em nuvem |

## Configurações

```bash
OFICINA_EMAIL_ENABLED=true
OFICINA_EMAIL_MODE=SMTP
OFICINA_EMAIL_REMETENTE=no-reply@suaoficina.com
SMTP_HOST=smtp.seudominio.com
SMTP_PORT=587
SMTP_USERNAME=usuario-smtp
SMTP_PASSWORD=senha-smtp
SMTP_AUTH=true
SMTP_STARTTLS_ENABLE=true
```

Em ambiente AWS via GitHub Actions, `SMTP_USERNAME` e `SMTP_PASSWORD` devem ser tratados como GitHub Secrets e aplicados no Kubernetes `Secret`. As demais configuracoes SMTP nao sensiveis podem ser mantidas como Repository Variables ou Secrets e sao aplicadas no `ConfigMap` durante a esteira de deploy da aplicacao.

Para demonstracoes com Mailtrap Sandbox, considerar o limite operacional do plano usado. No plano gratuito validado, o limite era de 1 e-mail a cada 10 segundos por sandbox. A collection Postman deve usar delay suficiente entre requests que disparam e-mail, recomendado `15000 ms` ou maior, para evitar o erro SMTP:

```text
550 5.7.0 Too many emails per second. Please upgrade your plan
```

Quando esse limite e atingido, notificacoes de status podem falhar sem bloquear a transicao da OS, por decisao desta ADR. Fluxos em que o e-mail faz parte da resposta esperada, como notificacao de orcamento com links de decisao, podem retornar erro HTTP se o provedor SMTP recusar o envio.

## Status Notificados

- `RECEBIDA`
- `EM_DIAGNOSTICO`
- `AGUARDANDO_APROVACAO`
- `EM_EXECUCAO`
- `FINALIZADA`
- `AGUARDANDO_RETIRADA`
- `ENTREGUE`
- `CANCELADA`

## Relação com Requisitos

Esta ADR atende ao RF41 da especificação de requisitos e complementa o RF28, que trata da consulta pública de status da OS.
