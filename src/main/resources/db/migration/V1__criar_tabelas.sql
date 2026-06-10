CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    documento VARCHAR(18) NOT NULL UNIQUE,
    tipo_documento VARCHAR(4) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(255)
);

CREATE TABLE veiculos (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(7) NOT NULL UNIQUE,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INTEGER NOT NULL,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id)
);

CREATE TABLE servicos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor_unitario NUMERIC(12,2) NOT NULL,
    tempo_estimado_minutos INTEGER
);

CREATE TABLE pecas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    quantidade_estoque INTEGER NOT NULL DEFAULT 0,
    valor_unitario NUMERIC(12,2) NOT NULL
);

CREATE TABLE ordens_servico (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(30) NOT NULL DEFAULT 'RECEBIDA',
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_finalizacao TIMESTAMP,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    veiculo_id BIGINT NOT NULL REFERENCES veiculos(id)
);

CREATE TABLE itens_os (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    valor_unitario NUMERIC(12,2) NOT NULL,
    referencia_id BIGINT,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id)
);

CREATE TABLE diagnosticos (
    id BIGSERIAL PRIMARY KEY,
    descricao_problema TEXT,
    data_diagnostico TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
);

CREATE TABLE execucoes (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO',
    diagnostico_id BIGINT REFERENCES diagnosticos(id),
    data_inicio TIMESTAMP,
    data_fim TIMESTAMP
);

CREATE TABLE orcamentos (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    valor_total NUMERIC(12,2) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_validade TIMESTAMP
);

CREATE TABLE pagamentos (
    id BIGSERIAL PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL REFERENCES ordens_servico(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    metodo_pagamento VARCHAR(20),
    valor NUMERIC(12,2) NOT NULL,
    data_pagamento TIMESTAMP
);

-- Índices para consultas frequentes
CREATE INDEX idx_veiculos_cliente ON veiculos(cliente_id);
CREATE INDEX idx_ordens_servico_cliente ON ordens_servico(cliente_id);
CREATE INDEX idx_ordens_servico_veiculo ON ordens_servico(veiculo_id);
CREATE INDEX idx_ordens_servico_status ON ordens_servico(status);
CREATE INDEX idx_itens_os_ordem ON itens_os(ordem_servico_id);
CREATE INDEX idx_orcamentos_ordem ON orcamentos(ordem_servico_id);
CREATE INDEX idx_execucoes_ordem ON execucoes(ordem_servico_id);
CREATE INDEX idx_pagamentos_ordem ON pagamentos(ordem_servico_id);
