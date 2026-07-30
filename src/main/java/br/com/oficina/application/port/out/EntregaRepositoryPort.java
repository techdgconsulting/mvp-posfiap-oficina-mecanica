package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Entrega;
import java.util.Optional;

public interface EntregaRepositoryPort {
    Entrega salvar(Entrega entrega);
    Optional<Entrega> buscarPorId(Long id);
    Optional<Entrega> buscarPorOrdemDeServico(Long ordemServicoId);
}
