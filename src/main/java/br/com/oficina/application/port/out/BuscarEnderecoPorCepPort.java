package br.com.oficina.application.port.out;

import br.com.oficina.application.query.EnderecoResult;
import java.util.Optional;

public interface BuscarEnderecoPorCepPort {
    Optional<EnderecoResult> buscarPorCep(String cep);
}
