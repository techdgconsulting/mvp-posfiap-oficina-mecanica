package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarClienteCommand;
import br.com.oficina.application.exception.ClienteJaExisteException;
import br.com.oficina.application.port.in.CriarClienteInputPort;
import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.query.ClienteResult;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.valueobject.CpfCnpj;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarClienteUseCase implements CriarClienteInputPort {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final BuscarEnderecoPorCepPort buscarEnderecoPorCepPort;

    @Override
    public ClienteResult execute(CriarClienteCommand command) {
        var documento = new CpfCnpj(command.documento());
        if (clienteRepositoryPort.existePorDocumento(documento.getValor())) {
            throw new ClienteJaExisteException("Ja existe um cliente com esse documento");
        }

        var cliente = new Cliente(documento, command.nome(), command.telefone(), command.email());
        if (command.cep() != null && !command.cep().isBlank()) {
            buscarEnderecoPorCepPort.buscarPorCep(command.cep()).ifPresent(endereco ->
                cliente.preencherEndereco(
                    endereco.cep(),
                    endereco.logradouro(),
                    endereco.bairro(),
                    endereco.cidade(),
                    endereco.uf()
                )
            );
        }

        return ClienteResultMapper.toResult(clienteRepositoryPort.salvar(cliente));
    }
}
