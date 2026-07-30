package br.com.oficina.application.usecase;

import br.com.oficina.application.query.ClienteResult;
import br.com.oficina.domain.model.Cliente;

final class ClienteResultMapper {

    private ClienteResultMapper() {
    }

    static ClienteResult toResult(Cliente c) {
        return new ClienteResult(
            c.getId(),
            c.getDocumento().formatado(),
            c.getDocumento().getTipo().name(),
            c.getNome(),
            c.getTelefone(),
            c.getEmail(),
            c.getCep(),
            c.getLogradouro(),
            c.getBairro(),
            c.getCidade(),
            c.getUf()
        );
    }
}
