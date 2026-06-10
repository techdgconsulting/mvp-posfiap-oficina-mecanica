-- Corrige inconsistência de dados: valor 'DIAGNOSTICO' foi renomeado para 'EM_DIAGNOSTICO' no enum StatusExecucao
UPDATE execucoes SET status = 'EM_DIAGNOSTICO' WHERE status = 'DIAGNOSTICO';
