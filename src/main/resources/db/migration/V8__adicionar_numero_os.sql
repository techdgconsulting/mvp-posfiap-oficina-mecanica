-- Adiciona coluna numero na tabela ordens_servico
ALTER TABLE ordens_servico ADD COLUMN numero VARCHAR(20);

-- Popula o número para registros existentes: OS-AAAA-NNNNN
UPDATE ordens_servico
SET numero = 'OS-' || EXTRACT(YEAR FROM data_criacao)::TEXT || '-' || LPAD(id::TEXT, 5, '0');

-- Aplica restrições após popular
ALTER TABLE ordens_servico ALTER COLUMN numero SET NOT NULL;
ALTER TABLE ordens_servico ADD CONSTRAINT uq_ordens_servico_numero UNIQUE (numero);
