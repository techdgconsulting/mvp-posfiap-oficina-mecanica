package br.com.oficina.application.usecase;

import br.com.oficina.application.query.EntregaResult;
import br.com.oficina.domain.model.Entrega;

final class EntregaResultMapper {

    private EntregaResultMapper() {
    }

    static EntregaResult toResult(Entrega entrega) {
        return new EntregaResult(
                entrega.getId(),
                entrega.getOrdemDeServicoId(),
                entrega.getStatus().name(),
                entrega.getDataEntrega(),
                entrega.getObservacoes());
    }
}
