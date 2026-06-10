package br.com.oficina.domain.entrega;

import java.util.Optional;

public interface EntregaRepository {
    Entrega salvar(Entrega entrega);
    Optional<Entrega> buscarPorId(Long id);
    Optional<Entrega> buscarPorOrdemDeServico(Long ordemDeServicoId);
}
