package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarClientePorDocumentoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.query.ClienteResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarClientePorDocumentoUseCase implements BuscarClientePorDocumentoInputPort {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public ClienteResult execute(String documento) {
        String doc = documento.replaceAll("[^\\d]", "");
        return clienteRepositoryPort.buscarPorDocumento(doc)
                .map(ClienteResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado com documento: " + documento));
    }
}
