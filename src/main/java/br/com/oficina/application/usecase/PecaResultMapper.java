package br.com.oficina.application.usecase;

import br.com.oficina.application.query.PecaResult;
import br.com.oficina.domain.model.Peca;

final class PecaResultMapper {

    private PecaResultMapper() {
    }

    static PecaResult toResult(Peca peca) {
        return new PecaResult(
                peca.getId(),
                peca.getNome(),
                peca.getDescricao(),
                peca.getQuantidadeEstoqueValor(),
                peca.getValorUnitario(),
                peca.getEstoqueMinimo(),
                peca.estaComEstoqueBaixo());
    }
}
