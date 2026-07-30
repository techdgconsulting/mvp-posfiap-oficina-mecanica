package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.valueobject.StatusExecucao;
import java.util.List;
import java.util.Optional;

public interface ExecucaoRepositoryPort {
    Execucao salvar(Execucao execucao);
    Optional<Execucao> buscarPorId(Long id);
    Optional<Execucao> buscarPorOrdemDeServico(Long ordemServicoId);
    List<Execucao> listarPorStatus(StatusExecucao status);
}
