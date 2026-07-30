CREATE TABLE orcamento_decisao_cliente (
    id BIGSERIAL PRIMARY KEY,
    orcamento_id BIGINT NOT NULL,
    ordem_servico_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    data_decisao TIMESTAMP,
    email_destino VARCHAR(180) NOT NULL,
    CONSTRAINT fk_orcamento_decisao_cliente_orcamento
        FOREIGN KEY (orcamento_id) REFERENCES orcamentos(id),
    CONSTRAINT fk_orcamento_decisao_cliente_ordem_servico
        FOREIGN KEY (ordem_servico_id) REFERENCES ordens_servico(id)
);

CREATE INDEX idx_orcamento_decisao_cliente_token_hash
    ON orcamento_decisao_cliente(token_hash);

CREATE INDEX idx_orcamento_decisao_cliente_orcamento_status
    ON orcamento_decisao_cliente(orcamento_id, status);
