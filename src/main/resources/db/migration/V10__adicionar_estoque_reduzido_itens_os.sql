-- Controla se o estoque da peça já foi baixado para este item.
-- Evita dupla redução de estoque no fluxo "novo problema":
-- quando gerarOrcamento() é chamado novamente (OS em EM_EXECUCAO),
-- aprovarOrcamento() só baixa peças cujo estoque ainda não foi reduzido.
ALTER TABLE itens_os ADD COLUMN estoque_reduzido BOOLEAN NOT NULL DEFAULT false;
