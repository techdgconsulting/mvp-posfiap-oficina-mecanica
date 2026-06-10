-- V12: Registrar o atendente responsável pela abertura da OS
-- atendenteNome em ordens_servico: capturado do JWT no momento de criarOS()
ALTER TABLE ordens_servico ADD COLUMN atendente_nome VARCHAR(150);
