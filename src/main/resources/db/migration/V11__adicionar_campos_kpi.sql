-- V11: Campos necessários para o Painel de KPIs (Encerramentos)
-- dataAprovacao em orcamentos: registra quando o cliente aprovou o orçamento
-- mecanicoNome em execucoes: registra quem executou o serviço (identificação do usuário autenticado via JWT)

ALTER TABLE orcamentos ADD COLUMN data_aprovacao TIMESTAMP;
ALTER TABLE execucoes  ADD COLUMN mecanico_nome  VARCHAR(150);
