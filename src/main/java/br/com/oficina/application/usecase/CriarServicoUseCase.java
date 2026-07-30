package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarServicoCommand;
import br.com.oficina.application.port.in.CriarServicoInputPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.query.ServicoResult;
import br.com.oficina.domain.model.Servico;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarServicoUseCase implements CriarServicoInputPort {

    private final ServicoRepositoryPort servicoRepository;

    @Override
    public ServicoResult execute(CriarServicoCommand command) {
        var servico = new Servico(
                command.nome(),
                command.descricao(),
                command.valorUnitario(),
                command.tempoEstimadoMinutos());
        return ServicoResultMapper.toResult(servicoRepository.salvar(servico));
    }
}
