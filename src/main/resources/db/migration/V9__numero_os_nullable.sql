-- O campo numero é preenchido em dois passos na aplicação:
-- 1) INSERT sem numero (para obter o ID gerado)
-- 2) UPDATE com numero = 'OS-YEAR-ID'
ALTER TABLE ordens_servico ALTER COLUMN numero DROP NOT NULL;
