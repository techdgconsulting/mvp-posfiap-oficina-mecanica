package br.com.oficina.adapters.in.web.mapper;

import br.com.oficina.adapters.in.web.request.PecaRequest;
import br.com.oficina.adapters.in.web.response.DisponibilidadePecaResponse;
import br.com.oficina.adapters.in.web.response.PecaResponse;
import br.com.oficina.application.command.AtualizarPecaCommand;
import br.com.oficina.application.command.CriarPecaCommand;
import br.com.oficina.application.query.DisponibilidadePecaResult;
import br.com.oficina.application.query.PecaResult;
import org.springframework.stereotype.Component;

@Component
public class PecaWebMapper {

    public CriarPecaCommand toCriarCommand(PecaRequest request) {
        return new CriarPecaCommand(
                request.nome(),
                request.descricao(),
                request.quantidadeEstoque(),
                request.valorUnitario(),
                request.estoqueMinimo());
    }

    public AtualizarPecaCommand toAtualizarCommand(Long id, PecaRequest request) {
        return new AtualizarPecaCommand(
                id,
                request.nome(),
                request.descricao(),
                request.quantidadeEstoque(),
                request.valorUnitario(),
                request.estoqueMinimo());
    }

    public PecaResponse toResponse(PecaResult result) {
        return new PecaResponse(
                result.id(),
                TextSecuritySanitizer.sanitize(result.nome()),
                TextSecuritySanitizer.sanitize(result.descricao()),
                result.quantidadeEstoque(),
                result.valorUnitario(),
                result.estoqueMinimo(),
                result.estoqueBaixo());
    }

    public DisponibilidadePecaResponse toResponse(DisponibilidadePecaResult result) {
        return new DisponibilidadePecaResponse(
                result.pecaId(),
                result.quantidadeSolicitada(),
                result.quantidadeDisponivel(),
                result.disponivel());
    }
}
