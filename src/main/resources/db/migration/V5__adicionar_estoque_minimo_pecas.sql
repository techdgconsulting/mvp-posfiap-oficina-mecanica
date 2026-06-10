-- V6: Adiciona campo estoque_minimo à tabela pecas
-- Cada peça passa a ter seu próprio limite mínimo de estoque (default: 5)

ALTER TABLE pecas ADD COLUMN estoque_minimo INTEGER NOT NULL DEFAULT 5;
