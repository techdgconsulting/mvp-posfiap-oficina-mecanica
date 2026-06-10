-- =====================================================
-- MASSA DE DADOS - 8 cenários completos
-- Cada cenário: Cliente + Veículo + Peças + Serviços + OS + Itens + Orçamento + Execução + Pagamento + Entrega + Encerramento
-- =====================================================

-- =====================================================
-- PEÇAS (catálogo compartilhado)
-- =====================================================
INSERT INTO pecas (nome, descricao, quantidade_estoque, valor_unitario, estoque_minimo) VALUES
('Pastilha de Freio Dianteira', 'Pastilha cerâmica para freio dianteiro', 50, 189.90, 10),
('Filtro de Óleo', 'Filtro de óleo do motor', 80, 45.00, 20),
('Óleo Motor 5W30 (litro)', 'Óleo sintético 5W30 - 1 litro', 120, 42.90, 30),
('Correia Dentada', 'Correia dentada do motor', 20, 320.00, 5),
('Vela de Ignição', 'Vela de ignição iridium', 60, 78.50, 15),
('Amortecedor Dianteiro', 'Amortecedor dianteiro par', 15, 580.00, 4),
('Bateria 60Ah', 'Bateria automotiva 60Ah', 25, 420.00, 5),
('Disco de Freio Dianteiro', 'Disco ventilado dianteiro', 30, 250.00, 8),
('Filtro de Ar', 'Filtro de ar do motor', 70, 65.00, 20),
('Bomba dÁgua', 'Bomba de água do motor', 12, 390.00, 3),
('Kit Embreagem', 'Kit completo de embreagem (platô + disco + rolamento)', 10, 850.00, 3),
('Tensor da Correia', 'Tensor da correia dentada', 18, 210.00, 5);

-- =====================================================
-- SERVIÇOS (catálogo compartilhado)
-- =====================================================
INSERT INTO servicos (nome, descricao, valor_unitario, tempo_estimado_minutos) VALUES
('Troca de Óleo', 'Troca de óleo do motor com filtro', 80.00, 30),
('Alinhamento e Balanceamento', 'Alinhamento de direção e balanceamento das 4 rodas', 150.00, 60),
('Troca de Pastilha de Freio', 'Substituição das pastilhas de freio dianteiras', 120.00, 45),
('Revisão Completa', 'Revisão dos principais componentes do veículo', 350.00, 180),
('Troca de Correia Dentada', 'Substituição da correia dentada com tensor', 280.00, 120),
('Troca de Amortecedor', 'Substituição de amortecedores (par dianteiro ou traseiro)', 200.00, 90),
('Troca de Bateria', 'Substituição da bateria com teste do alternador', 60.00, 20),
('Diagnóstico Eletrônico', 'Scanner completo do sistema eletrônico do veículo', 100.00, 40),
('Troca de Embreagem', 'Substituição do kit de embreagem completo', 600.00, 240),
('Higienização do Ar-Condicionado', 'Limpeza e higienização do sistema de ar-condicionado', 130.00, 50);

-- =====================================================
-- CENÁRIO 1: Troca de óleo simples - FLUXO COMPLETO (ENCERRADA)
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('52398614809', 'CPF', 'Carlos Alberto Silva', '11987654321', 'carlos.silva@email.com', '01310100', 'Av Paulista', 'Bela Vista', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('BRA2E19', 'Toyota', 'Corolla XEi', 2022, (SELECT id FROM clientes WHERE documento = '52398614809'));

INSERT INTO ordens_servico (status, data_criacao, data_finalizacao, cliente_id, veiculo_id)
VALUES ('ENTREGUE', '2026-05-01 08:30:00', '2026-05-01 10:00:00',
    (SELECT id FROM clientes WHERE documento = '52398614809'),
    (SELECT id FROM veiculos WHERE placa = 'BRA2E19'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Troca de Óleo', 1, 80.00, (SELECT id FROM servicos WHERE nome = 'Troca de Óleo'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE')),
('PECA', 'Filtro de Óleo', 1, 45.00, (SELECT id FROM pecas WHERE nome = 'Filtro de Óleo'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE')),
('PECA', 'Óleo Motor 5W30 (litro)', 4, 42.90, (SELECT id FROM pecas WHERE nome = 'Óleo Motor 5W30 (litro)'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE'));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE'),
    'APROVADO', 296.60, '2026-05-01 08:45:00', '2026-05-08 08:45:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Óleo do motor degradado, filtro saturado', '2026-05-01 08:35:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE'),
    'SERVICO_FINALIZADO', (SELECT MAX(id) FROM diagnosticos), '2026-05-01 09:00:00', '2026-05-01 09:30:00');

INSERT INTO pagamentos (ordem_servico_id, status, metodo_pagamento, valor, data_pagamento)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE'),
    'APROVADO', 'PIX', 296.60, '2026-05-01 09:35:00');

INSERT INTO entregas (ordem_servico_id, status, data_entrega, observacoes)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE'),
    'VEICULO_ENTREGUE', '2026-05-01 10:00:00', 'Próxima troca de óleo em 5.000 km');

INSERT INTO encerramentos (ordem_servico_id, status, data_encerramento)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'BRA2E19') AND status = 'ENTREGUE'),
    'ENCERRADO', '2026-05-01 10:00:00');

-- =====================================================
-- CENÁRIO 2: Revisão completa - FLUXO COMPLETO (ENCERRADA)
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('83712946000180', 'CNPJ', 'Transportadora Rápido Ltda', '1133445566', 'contato@rapido.com.br', '04543011', 'Rua Funchal', 'Vila Olímpia', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('RIO4F56', 'Volkswagen', 'Delivery 9.170', 2021, (SELECT id FROM clientes WHERE documento = '83712946000180'));

INSERT INTO ordens_servico (status, data_criacao, data_finalizacao, cliente_id, veiculo_id)
VALUES ('ENTREGUE', '2026-04-20 07:00:00', '2026-04-20 14:30:00',
    (SELECT id FROM clientes WHERE documento = '83712946000180'),
    (SELECT id FROM veiculos WHERE placa = 'RIO4F56'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Revisão Completa', 1, 350.00, (SELECT id FROM servicos WHERE nome = 'Revisão Completa'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56'))),
('SERVICO', 'Troca de Óleo', 1, 80.00, (SELECT id FROM servicos WHERE nome = 'Troca de Óleo'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56'))),
('PECA', 'Filtro de Óleo', 1, 45.00, (SELECT id FROM pecas WHERE nome = 'Filtro de Óleo'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56'))),
('PECA', 'Óleo Motor 5W30 (litro)', 6, 42.90, (SELECT id FROM pecas WHERE nome = 'Óleo Motor 5W30 (litro)'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56'))),
('PECA', 'Filtro de Ar', 1, 65.00, (SELECT id FROM pecas WHERE nome = 'Filtro de Ar'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56'))),
('PECA', 'Vela de Ignição', 4, 78.50, (SELECT id FROM pecas WHERE nome = 'Vela de Ignição'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56')));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56')),
    'APROVADO', 1111.40, '2026-04-20 07:30:00', '2026-04-27 07:30:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Revisão programada 40.000km - filtros saturados, velas desgastadas', '2026-04-20 07:15:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56')),
    'SERVICO_FINALIZADO', (SELECT MAX(id) FROM diagnosticos), '2026-04-20 08:00:00', '2026-04-20 13:00:00');

INSERT INTO pagamentos (ordem_servico_id, status, metodo_pagamento, valor, data_pagamento)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56')),
    'APROVADO', 'BOLETO', 1111.40, '2026-04-20 13:30:00');

INSERT INTO entregas (ordem_servico_id, status, data_entrega, observacoes)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56')),
    'VEICULO_ENTREGUE', '2026-04-20 14:30:00', 'Revisão completa realizada. Próxima revisão em 50.000km');

INSERT INTO encerramentos (ordem_servico_id, status, data_encerramento)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'RIO4F56')),
    'ENCERRADO', '2026-04-20 14:30:00');

-- =====================================================
-- CENÁRIO 3: Freios + Amortecedores - EM EXECUÇÃO
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('31856742090', 'CPF', 'Maria Fernanda Oliveira', '11976543210', 'maria.oliveira@email.com', '04038001', 'Rua Domingos de Morais', 'Vila Mariana', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('SPO3A77', 'Honda', 'Civic Touring', 2023, (SELECT id FROM clientes WHERE documento = '31856742090'));

INSERT INTO ordens_servico (status, data_criacao, cliente_id, veiculo_id)
VALUES ('EM_EXECUCAO', '2026-05-18 08:00:00',
    (SELECT id FROM clientes WHERE documento = '31856742090'),
    (SELECT id FROM veiculos WHERE placa = 'SPO3A77'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Troca de Pastilha de Freio', 1, 120.00, (SELECT id FROM servicos WHERE nome = 'Troca de Pastilha de Freio'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77'))),
('SERVICO', 'Troca de Amortecedor', 1, 200.00, (SELECT id FROM servicos WHERE nome = 'Troca de Amortecedor'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77'))),
('PECA', 'Pastilha de Freio Dianteira', 1, 189.90, (SELECT id FROM pecas WHERE nome = 'Pastilha de Freio Dianteira'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77'))),
('PECA', 'Disco de Freio Dianteiro', 2, 250.00, (SELECT id FROM pecas WHERE nome = 'Disco de Freio Dianteiro'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77'))),
('PECA', 'Amortecedor Dianteiro', 1, 580.00, (SELECT id FROM pecas WHERE nome = 'Amortecedor Dianteiro'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77')));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77')),
    'APROVADO', 1589.90, '2026-05-18 08:30:00', '2026-05-25 08:30:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Pastilhas gastas abaixo do limite, discos com desgaste irregular, amortecedores vazando', '2026-05-18 08:15:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'SPO3A77')),
    'EM_ANDAMENTO', (SELECT MAX(id) FROM diagnosticos), '2026-05-18 09:00:00', NULL);

-- =====================================================
-- CENÁRIO 4: Correia Dentada - AGUARDANDO APROVAÇÃO DO ORÇAMENTO
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('67493821054', 'CPF', 'Roberto Santos Filho', '11965432109', 'roberto.santos@email.com', '03036000', 'Rua Bresser', 'Brás', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('MGA5B23', 'Fiat', 'Toro Ranch', 2020, (SELECT id FROM clientes WHERE documento = '67493821054'));

INSERT INTO ordens_servico (status, data_criacao, cliente_id, veiculo_id)
VALUES ('AGUARDANDO_APROVACAO', '2026-05-17 14:00:00',
    (SELECT id FROM clientes WHERE documento = '67493821054'),
    (SELECT id FROM veiculos WHERE placa = 'MGA5B23'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Troca de Correia Dentada', 1, 280.00, (SELECT id FROM servicos WHERE nome = 'Troca de Correia Dentada'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'MGA5B23'))),
('PECA', 'Correia Dentada', 1, 320.00, (SELECT id FROM pecas WHERE nome = 'Correia Dentada'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'MGA5B23'))),
('PECA', 'Tensor da Correia', 1, 210.00, (SELECT id FROM pecas WHERE nome = 'Tensor da Correia'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'MGA5B23'))),
('PECA', 'Bomba dÁgua', 1, 390.00, (SELECT id FROM pecas WHERE nome = 'Bomba dÁgua'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'MGA5B23')));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'MGA5B23')),
    'ENVIADO', 1200.00, '2026-05-17 14:30:00', '2026-05-24 14:30:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Correia dentada com rachaduras, tensor com folga, bomba de água com vazamento incipiente', '2026-05-17 14:20:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'MGA5B23')),
    'AGUARDANDO', (SELECT MAX(id) FROM diagnosticos), NULL, NULL);

-- =====================================================
-- CENÁRIO 5: Troca de Embreagem - FINALIZADA (Aguardando pagamento)
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('45102983067', 'CPF', 'Ana Paula Mendes', '11954321098', 'ana.mendes@email.com', '05407002', 'Rua Augusta', 'Cerqueira César', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('GRU7C88', 'Chevrolet', 'Onix Plus', 2019, (SELECT id FROM clientes WHERE documento = '45102983067'));

INSERT INTO ordens_servico (status, data_criacao, data_finalizacao, cliente_id, veiculo_id)
VALUES ('FINALIZADA', '2026-05-16 09:00:00', '2026-05-17 11:00:00',
    (SELECT id FROM clientes WHERE documento = '45102983067'),
    (SELECT id FROM veiculos WHERE placa = 'GRU7C88'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Troca de Embreagem', 1, 600.00, (SELECT id FROM servicos WHERE nome = 'Troca de Embreagem'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'GRU7C88'))),
('PECA', 'Kit Embreagem', 1, 850.00, (SELECT id FROM pecas WHERE nome = 'Kit Embreagem'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'GRU7C88')));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'GRU7C88')),
    'APROVADO', 1450.00, '2026-05-16 09:30:00', '2026-05-23 09:30:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Embreagem patinando, disco desgastado, rolamento com ruído', '2026-05-16 09:20:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'GRU7C88')),
    'SERVICO_FINALIZADO', (SELECT MAX(id) FROM diagnosticos), '2026-05-16 10:00:00', '2026-05-17 11:00:00');

INSERT INTO pagamentos (ordem_servico_id, status, metodo_pagamento, valor, data_pagamento)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'GRU7C88')),
    'PENDENTE', 'CARTAO_CREDITO', 1450.00, NULL);

-- =====================================================
-- CENÁRIO 6: Diagnóstico eletrônico + Bateria - EM DIAGNÓSTICO
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('19284756000195', 'CNPJ', 'Auto Escola Progresso ME', '1129876543', 'financeiro@autoescolaprogresso.com.br', '02012000', 'Rua Voluntários da Pátria', 'Santana', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('ABC1D23', 'Hyundai', 'HB20 Comfort', 2024, (SELECT id FROM clientes WHERE documento = '19284756000195'));

INSERT INTO ordens_servico (status, data_criacao, cliente_id, veiculo_id)
VALUES ('EM_DIAGNOSTICO', '2026-05-18 10:00:00',
    (SELECT id FROM clientes WHERE documento = '19284756000195'),
    (SELECT id FROM veiculos WHERE placa = 'ABC1D23'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Diagnóstico Eletrônico', 1, 100.00, (SELECT id FROM servicos WHERE nome = 'Diagnóstico Eletrônico'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'ABC1D23')));

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Veículo não dá partida - investigando sistema elétrico', '2026-05-18 10:15:00', 'EM_ANDAMENTO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'ABC1D23')),
    'DIAGNOSTICO', (SELECT MAX(id) FROM diagnosticos), '2026-05-18 10:15:00', NULL);

-- =====================================================
-- CENÁRIO 7: Troca de bateria rápida - PAGAMENTO RECUSADO
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('78932145600', 'CPF', 'Fernando Costa Lima', '11943210987', 'fernando.lima@email.com', '01001000', 'Praça da Sé', 'Sé', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('XYZ9H01', 'Renault', 'Kwid Zen', 2021, (SELECT id FROM clientes WHERE documento = '78932145600'));

INSERT INTO ordens_servico (status, data_criacao, data_finalizacao, cliente_id, veiculo_id)
VALUES ('FINALIZADA', '2026-05-17 16:00:00', '2026-05-17 16:40:00',
    (SELECT id FROM clientes WHERE documento = '78932145600'),
    (SELECT id FROM veiculos WHERE placa = 'XYZ9H01'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Troca de Bateria', 1, 60.00, (SELECT id FROM servicos WHERE nome = 'Troca de Bateria'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'XYZ9H01'))),
('PECA', 'Bateria 60Ah', 1, 420.00, (SELECT id FROM pecas WHERE nome = 'Bateria 60Ah'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'XYZ9H01')));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'XYZ9H01')),
    'APROVADO', 480.00, '2026-05-17 16:05:00', '2026-05-24 16:05:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Bateria sem carga, teste indica fim de vida útil', '2026-05-17 16:05:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'XYZ9H01')),
    'SERVICO_FINALIZADO', (SELECT MAX(id) FROM diagnosticos), '2026-05-17 16:10:00', '2026-05-17 16:40:00');

INSERT INTO pagamentos (ordem_servico_id, status, metodo_pagamento, valor, data_pagamento)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'XYZ9H01')),
    'RECUSADO', 'CARTAO_CREDITO', 480.00, NULL);

-- =====================================================
-- CENÁRIO 8: OS Cancelada - ORÇAMENTO REJEITADO
-- =====================================================
INSERT INTO clientes (documento, tipo_documento, nome, telefone, email, cep, logradouro, bairro, cidade, uf)
VALUES ('92456178033', 'CPF', 'Juliana Rocha Pereira', '11932109876', 'juliana.pereira@email.com', '04101300', 'Rua Vergueiro', 'Liberdade', 'São Paulo', 'SP');

INSERT INTO veiculos (placa, marca, modelo, ano, cliente_id)
VALUES ('NPR8J45', 'Ford', 'Ka SE', 2018, (SELECT id FROM clientes WHERE documento = '92456178033'));

INSERT INTO ordens_servico (status, data_criacao, cliente_id, veiculo_id)
VALUES ('CANCELADA', '2026-05-15 11:00:00',
    (SELECT id FROM clientes WHERE documento = '92456178033'),
    (SELECT id FROM veiculos WHERE placa = 'NPR8J45'));

INSERT INTO itens_os (tipo, descricao, quantidade, valor_unitario, referencia_id, ordem_servico_id)
VALUES
('SERVICO', 'Troca de Correia Dentada', 1, 280.00, (SELECT id FROM servicos WHERE nome = 'Troca de Correia Dentada'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45'))),
('SERVICO', 'Troca de Amortecedor', 1, 200.00, (SELECT id FROM servicos WHERE nome = 'Troca de Amortecedor'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45'))),
('PECA', 'Correia Dentada', 1, 320.00, (SELECT id FROM pecas WHERE nome = 'Correia Dentada'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45'))),
('PECA', 'Tensor da Correia', 1, 210.00, (SELECT id FROM pecas WHERE nome = 'Tensor da Correia'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45'))),
('PECA', 'Amortecedor Dianteiro', 1, 580.00, (SELECT id FROM pecas WHERE nome = 'Amortecedor Dianteiro'), (SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45')));

INSERT INTO orcamentos (ordem_servico_id, status, valor_total, data_criacao, data_validade)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45')),
    'REJEITADO', 1590.00, '2026-05-15 11:30:00', '2026-05-22 11:30:00');

INSERT INTO diagnosticos (descricao_problema, data_diagnostico, status)
VALUES ('Correia dentada ressecada, amortecedores com vazamento', '2026-05-15 11:15:00', 'CONCLUIDO');

INSERT INTO execucoes (ordem_servico_id, status, diagnostico_id, data_inicio, data_fim)
VALUES ((SELECT id FROM ordens_servico WHERE veiculo_id = (SELECT id FROM veiculos WHERE placa = 'NPR8J45')),
    'CANCELADA', (SELECT MAX(id) FROM diagnosticos), NULL, NULL);
