CREATE TABLE entregas (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_LIBERACAO',
    data_entrega TIMESTAMP,
    observacoes TEXT
);

CREATE TABLE encerramentos (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_encerramento TIMESTAMP
);

CREATE INDEX idx_entregas_ordem ON entregas(ordem_servico_id);
CREATE INDEX idx_encerramentos_ordem ON encerramentos(ordem_servico_id);
