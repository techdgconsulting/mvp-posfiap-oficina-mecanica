package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Encerramento;
import java.util.Optional;

public interface EncerramentoRepositoryPort {
    Encerramento salvar(Encerramento encerramento);
    Optional<Encerramento> buscarPorId(Long id);
    Optional<Encerramento> buscarPorOrdemDeServico(Long ordemServicoId);
}
