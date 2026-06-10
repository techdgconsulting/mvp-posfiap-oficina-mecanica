package br.com.oficina.domain.financeiro;

import java.util.Optional;

public interface PagamentoRepository {
    Pagamento salvar(Pagamento pagamento);
    Optional<Pagamento> buscarPorId(Long id);
    Optional<Pagamento> buscarPorOrdemDeServico(Long ordemDeServicoId);
}
