package br.com.oficina.application.usecase;

import br.com.oficina.application.query.ServicoResult;
import br.com.oficina.domain.model.Servico;

final class ServicoResultMapper {

    private ServicoResultMapper() {
    }

    static ServicoResult toResult(Servico servico) {
        return new ServicoResult(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getValorUnitario(),
                servico.getTempoEstimadoMinutos());
    }
}
