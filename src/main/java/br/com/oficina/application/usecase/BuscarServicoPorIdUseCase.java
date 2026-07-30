package br.com.oficina.application.usecase;

import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.BuscarServicoPorIdInputPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.query.ServicoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarServicoPorIdUseCase implements BuscarServicoPorIdInputPort {

    private final ServicoRepositoryPort servicoRepository;

    @Override
    public ServicoResult execute(Long id) {
        return servicoRepository.buscarPorId(id)
                .map(ServicoResultMapper::toResult)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico nao encontrado com id " + id));
    }
}
