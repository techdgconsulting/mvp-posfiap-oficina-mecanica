# ADR-012: Varreduras de Segurança com OWASP ZAP e Trivy

**Status:** Aceito  
**Data:** 2026-08-13  
**Autor:** Diego Gonzalez  
**Contexto do Projeto:** Sistema de Oficina Mecânica DGCar - Pós-graduação FIAP

## Contexto

O projeto expõe uma API REST protegida por JWT, executa em container Docker e empacota dependências Java no artefato `app.jar`. Além dos testes automatizados, é necessário validar riscos de segurança que aparecem somente em execução HTTP real ou na composição final da imagem Docker.

As varreduras devem apoiar a identificação de falhas antes da entrega, especialmente:

- exposição indevida de mensagens de erro;
- respostas `401/403/500` com detalhes técnicos;
- endpoints públicos ou autenticados com configuração incorreta;
- vulnerabilidades conhecidas em bibliotecas Java;
- vulnerabilidades conhecidas em pacotes do sistema operacional da imagem.

## Decisão

Adotar duas ferramentas complementares de segurança:

| Ferramenta | Tipo | Objetivo |
|------------|------|----------|
| OWASP ZAP | DAST | Analisar a API em execução por HTTP, usando o contrato OpenAPI/Swagger para descobrir endpoints e testar comportamento real da aplicação. |
| Trivy | SCA / Image Scan | Analisar a imagem Docker e o `app.jar`, identificando CVEs em pacotes do sistema operacional e dependências Java empacotadas. |

O OWASP ZAP deve ser usado após subir a aplicação localmente ou em ambiente de homologação. O contrato OpenAPI pode ser importado de:

```text
http://localhost:8080/api-docs
```

O Trivy deve ser executado sobre a imagem Docker gerada para a aplicação. Para evitar diferenças entre shells no Windows, o fluxo recomendado é exportar a imagem para `.tar` e analisá-la como arquivo:

```bash
docker build -t oficina-api:latest .
docker save oficina-api:latest -o oficina-api.tar
MSYS_NO_PATHCONV=1 docker run --rm -v "$PWD":/work aquasec/trivy image --input /work/oficina-api.tar --severity HIGH,CRITICAL
```

Em PowerShell, o volume pode ser montado com:

```powershell
docker run --rm -v "${PWD}:/work" aquasec/trivy image --input /work/oficina-api.tar --severity HIGH,CRITICAL
```

## Critérios De Avaliação

- Vulnerabilidades `CRITICAL` devem bloquear a entrega até correção, mitigação formal ou justificativa documentada.
- Vulnerabilidades `HIGH` devem ser corrigidas quando houver versão corrigida disponível.
- Vulnerabilidades sem versão corrigida devem ser avaliadas caso a caso, considerando exposição real, componente afetado e mitigação possível.
- Alertas informativos do ZAP devem ser documentados quando forem esperados, por exemplo identificação de endpoint de autenticação.
- Alertas de exposição de erro devem ser tratados preferencialmente por payloads sanitizados e logs internos.

## Evidências

As evidências podem ser salvas em `docs/ReportOWASP/` e `docs/ReportTRIVY/`, mantendo a data ou contexto da execução no nome do arquivo.

Exemplos:

```text
docs/ReportOWASP/zap-report-2026-08-13.html
docs/ReportTRIVY/trivy-image-report-2026-08-13.json
```

Relatórios antigos devem ser preservados como histórico quando forem relevantes para demonstrar evolução da correção.

## Consequências Positivas

- Aumenta a rastreabilidade das validações de segurança do MVP.
- Complementa testes automatizados com análise dinâmica e análise de composição de software.
- Facilita comprovação de correções, como sanitização de payloads de erro e atualização de dependências vulneráveis.
- Ajuda a identificar problemas na imagem final, não apenas no código-fonte.

## Consequências Negativas

- Pode aumentar o tempo de validação antes da entrega.
- Resultados dependem da base de vulnerabilidades disponível no momento da execução.
- O ZAP pode gerar falsos positivos ou alertas informativos que exigem análise manual.
- O Trivy pode apontar vulnerabilidades transitivas que dependem de atualização de bibliotecas ou da imagem base.

## Alternativas Consideradas

| Alternativa | Motivo da não adoção como solução única |
|-------------|------------------------------------------|
| Usar apenas testes automatizados | Testes funcionais não identificam todos os problemas de configuração HTTP, exposição de erro ou CVEs em dependências. |
| Usar apenas OWASP ZAP | Não cobre adequadamente vulnerabilidades conhecidas em pacotes da imagem e bibliotecas empacotadas. |
| Usar apenas Trivy | Não valida comportamento real da API em execução, autenticação, autorização e respostas HTTP. |
| Executar scans apenas manualmente sem documentação | Reduz rastreabilidade e dificulta repetir o processo de validação. |

## Relação Com Outros Documentos

- `README.md`: documenta comandos práticos para executar OWASP ZAP e Trivy.
- `ADR-004`: documenta respostas de erro sanitizadas e fallback `/error`.
- `ADR-009`: documenta a estratégia de testes automatizados, complementada por estas varreduras de segurança.
