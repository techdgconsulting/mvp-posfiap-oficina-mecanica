package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Orcamento;
import java.util.List;
import java.util.Optional;

public interface OrcamentoRepositoryPort {
    Orcamento salvar(Orcamento orcamento);
    Optional<Orcamento> buscarPorId(Long id);
    List<Orcamento> listarPorOrdemDeServico(Long ordemServicoId);
    Optional<Orcamento> buscarAtivoByOrdemDeServico(Long ordemServicoId);
}
