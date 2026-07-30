package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarPecaCommand;
import br.com.oficina.application.port.in.CriarPecaInputPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.query.PecaResult;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.valueobject.Quantidade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarPecaUseCase implements CriarPecaInputPort {

    private final PecaRepositoryPort pecaRepository;

    @Override
    public PecaResult execute(CriarPecaCommand command) {
        var builder = Peca.builder()
                .nome(command.nome())
                .descricao(command.descricao())
                .quantidadeEstoque(new Quantidade(command.quantidadeEstoque()))
                .valorUnitario(command.valorUnitario());
        if (command.estoqueMinimo() != null) {
            builder.estoqueMinimo(command.estoqueMinimo());
        }
        return PecaResultMapper.toResult(pecaRepository.salvar(builder.build()));
    }
}
