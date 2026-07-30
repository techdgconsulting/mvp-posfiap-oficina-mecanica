package br.com.oficina.application.usecase;

import br.com.oficina.application.query.OrcamentoResult;
import br.com.oficina.domain.model.Orcamento;

final class OrcamentoResultMapper {

    private OrcamentoResultMapper() {
    }

    static OrcamentoResult toResult(Orcamento orcamento) {
        return new OrcamentoResult(
                orcamento.getId(),
                orcamento.getOrdemDeServicoId(),
                orcamento.getStatus().name(),
                orcamento.getValorTotal(),
                orcamento.getDataCriacao(),
                orcamento.getDataValidade());
    }
}
