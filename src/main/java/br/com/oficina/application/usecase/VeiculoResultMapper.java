package br.com.oficina.application.usecase;

import br.com.oficina.application.query.VeiculoResult;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.Veiculo;

final class VeiculoResultMapper {

    private VeiculoResultMapper() {
    }

    static VeiculoResult toResult(Veiculo veiculo, Cliente cliente) {
        Long clienteId = veiculo.getClienteId() != null ? veiculo.getClienteId() : cliente != null ? cliente.getId() : null;
        String clienteNome = cliente != null ? cliente.getNome() : null;
        return new VeiculoResult(
            veiculo.getId(),
            veiculo.getPlaca().getValor(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getAno(),
            clienteId,
            clienteNome
        );
    }
}
