package br.com.oficina.domain.orcamento;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository {
    Orcamento salvar(Orcamento orcamento);
    Optional<Orcamento> buscarPorId(Long id);
    List<Orcamento> listarPorOrdemDeServico(Long ordemDeServicoId);
    Optional<Orcamento> buscarAtivoByOrdemDeServico(Long ordemDeServicoId);
}
