package br.com.oficina.domain.encerramento;

import java.util.Optional;

public interface EncerramentoRepository {
    Encerramento salvar(Encerramento encerramento);
    Optional<Encerramento> buscarPorId(Long id);
    Optional<Encerramento> buscarPorOrdemDeServico(Long ordemDeServicoId);
}
