package br.com.oficina.application.port.out;

import br.com.oficina.domain.model.Pagamento;
import java.util.Optional;

public interface PagamentoRepositoryPort {
    Pagamento salvar(Pagamento pagamento);
    Optional<Pagamento> buscarPorId(Long id);
    Optional<Pagamento> buscarPorOrdemDeServico(Long ordemServicoId);
}
