package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AtualizarServicoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarServicoInputPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.query.ServicoResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarServicoUseCase implements AtualizarServicoInputPort {

    private final ServicoRepositoryPort servicoRepository;

    @Override
    public ServicoResult execute(AtualizarServicoCommand command) {
        var servico = servicoRepository.buscarPorId(command.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Servico nao encontrado com id " + command.id()));
        servico.atualizar(
                command.nome(),
                command.descricao(),
                command.valorUnitario(),
                command.tempoEstimadoMinutos());
        return ServicoResultMapper.toResult(servicoRepository.salvar(servico));
    }
}
