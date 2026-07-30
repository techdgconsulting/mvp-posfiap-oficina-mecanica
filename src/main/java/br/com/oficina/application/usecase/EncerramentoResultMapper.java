package br.com.oficina.application.usecase;

import br.com.oficina.application.query.EncerramentoResult;
import br.com.oficina.domain.model.Encerramento;

final class EncerramentoResultMapper {

    private EncerramentoResultMapper() {
    }

    static EncerramentoResult toResult(Encerramento encerramento) {
        return new EncerramentoResult(
                encerramento.getId(),
                encerramento.getOrdemDeServicoId(),
                encerramento.getStatus().name(),
                encerramento.getDataEncerramento());
    }
}
