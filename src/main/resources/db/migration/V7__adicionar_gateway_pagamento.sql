-- V7: Adiciona campos de integração com gateway de pagamento à tabela pagamentos
-- transaction_id : identificador retornado pelo provedor (ex.: MOCK-uuid)
-- gateway_mensagem : mensagem textual de aprovação/recusa retornada pelo provedor

ALTER TABLE pagamentos ADD COLUMN transaction_id VARCHAR(60);
ALTER TABLE pagamentos ADD COLUMN gateway_mensagem VARCHAR(255);

CREATE INDEX idx_pagamentos_transaction ON pagamentos(transaction_id);
