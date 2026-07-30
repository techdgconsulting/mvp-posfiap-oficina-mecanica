package br.com.oficina.adapters.in.web.mapper;

import br.com.oficina.adapters.in.web.request.ClienteRequest;
import br.com.oficina.adapters.in.web.response.ClienteResponse;
import br.com.oficina.application.command.AtualizarClienteCommand;
import br.com.oficina.application.command.CriarClienteCommand;
import br.com.oficina.application.command.ExcluirClienteCommand;
import br.com.oficina.application.query.ClienteResult;
import org.springframework.stereotype.Component;

@Component
public class ClienteWebMapper {

    public CriarClienteCommand toCriarCommand(ClienteRequest request, String atendenteNome) {
        return new CriarClienteCommand(
            request.documento(),
            request.nome(),
            request.telefone(),
            request.email(),
            request.cep(),
            atendenteNome
        );
    }

    public AtualizarClienteCommand toAtualizarCommand(Long id, ClienteRequest request, String atendenteNome) {
        return new AtualizarClienteCommand(
            id,
            request.nome(),
            request.telefone(),
            request.email(),
            atendenteNome
        );
    }

    public ExcluirClienteCommand toExcluirCommand(Long id, String atendenteNome) {
        return new ExcluirClienteCommand(id, atendenteNome);
    }

    public ClienteResponse toResponse(ClienteResult result) {
        return new ClienteResponse(
            result.id(),
            result.documento(),
            result.tipoDocumento(),
            result.nome(),
            result.telefone(),
            result.email(),
            result.cep(),
            result.logradouro(),
            result.bairro(),
            result.cidade(),
            result.uf()
        );
    }
}
