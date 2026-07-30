package br.com.oficina.application.usecase;

import br.com.oficina.application.command.ExcluirServicoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.ExcluirServicoInputPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExcluirServicoUseCase implements ExcluirServicoInputPort {

    private final ServicoRepositoryPort servicoRepository;

    @Override
    public void execute(ExcluirServicoCommand command) {
        servicoRepository.buscarPorId(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico nao encontrado com id " + command.id()));
        servicoRepository.excluir(command.id());
    }
}
