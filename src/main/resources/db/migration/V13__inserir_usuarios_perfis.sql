-- Insere usuários de cada perfil para uso nos testes e desenvolvimento.
-- Senha de todos: senha123  (BCrypt cost=10)
INSERT INTO usuarios (username, password, role)
VALUES
    ('atendente1', '$2a$10$bfASOicSWcwGNT9vTrzbpeVWjlsB5NmXR4xbf4oRJqiyfMa.e1ZEK', 'ATENDENTE'),
    ('mecanico1',  '$2a$10$bfASOicSWcwGNT9vTrzbpeVWjlsB5NmXR4xbf4oRJqiyfMa.e1ZEK', 'MECANICO'),
    ('gestor1',    '$2a$10$bfASOicSWcwGNT9vTrzbpeVWjlsB5NmXR4xbf4oRJqiyfMa.e1ZEK', 'GESTOR');
