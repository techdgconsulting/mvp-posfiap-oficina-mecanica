package br.com.oficina.adapters.in.web.mapper;

import br.com.oficina.adapters.in.web.request.ServicoRequest;
import br.com.oficina.adapters.in.web.response.ServicoResponse;
import br.com.oficina.application.command.AtualizarServicoCommand;
import br.com.oficina.application.command.CriarServicoCommand;
import br.com.oficina.application.query.ServicoResult;
import org.springframework.stereotype.Component;

@Component
public class ServicoWebMapper {

    public CriarServicoCommand toCriarCommand(ServicoRequest request) {
        return new CriarServicoCommand(
                request.nome(),
                request.descricao(),
                request.valorUnitario(),
                request.tempoEstimadoMinutos());
    }

    public AtualizarServicoCommand toAtualizarCommand(Long id, ServicoRequest request) {
        return new AtualizarServicoCommand(
                id,
                request.nome(),
                request.descricao(),
                request.valorUnitario(),
                request.tempoEstimadoMinutos());
    }

    public ServicoResponse toResponse(ServicoResult result) {
        return new ServicoResponse(
                result.id(),
                TextSecuritySanitizer.sanitize(result.nome()),
                TextSecuritySanitizer.sanitize(result.descricao()),
                result.valorUnitario(),
                result.tempoEstimadoMinutos());
    }
}
