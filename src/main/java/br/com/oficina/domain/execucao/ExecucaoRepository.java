package br.com.oficina.domain.execucao;

import java.util.Optional;

public interface ExecucaoRepository {
    Execucao salvar(Execucao execucao);
    Optional<Execucao> buscarPorId(Long id);
    Optional<Execucao> buscarPorOrdemDeServico(Long ordemDeServicoId);
}
