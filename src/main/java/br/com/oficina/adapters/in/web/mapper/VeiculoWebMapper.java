package br.com.oficina.adapters.in.web.mapper;

import br.com.oficina.adapters.in.web.request.VeiculoRequest;
import br.com.oficina.adapters.in.web.response.VeiculoResponse;
import br.com.oficina.application.command.AtualizarVeiculoCommand;
import br.com.oficina.application.command.CriarVeiculoCommand;
import br.com.oficina.application.command.ExcluirVeiculoCommand;
import br.com.oficina.application.query.VeiculoResult;
import org.springframework.stereotype.Component;

@Component
public class VeiculoWebMapper {

    public CriarVeiculoCommand toCriarCommand(VeiculoRequest request) {
        return new CriarVeiculoCommand(
            request.placa(),
            request.marca(),
            request.modelo(),
            request.ano(),
            request.clienteId()
        );
    }

    public AtualizarVeiculoCommand toAtualizarCommand(Long id, VeiculoRequest request) {
        return new AtualizarVeiculoCommand(
            id,
            request.placa(),
            request.marca(),
            request.modelo(),
            request.ano(),
            request.clienteId()
        );
    }

    public ExcluirVeiculoCommand toExcluirCommand(Long id) {
        return new ExcluirVeiculoCommand(id);
    }

    public VeiculoResponse toResponse(VeiculoResult result) {
        return new VeiculoResponse(
            result.id(),
            result.placa(),
            result.marca(),
            result.modelo(),
            result.ano(),
            result.clienteId(),
            result.clienteNome()
        );
    }
}
