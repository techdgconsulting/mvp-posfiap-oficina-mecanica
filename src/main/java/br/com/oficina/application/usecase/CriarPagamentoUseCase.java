package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarPagamentoCommand;
import br.com.oficina.application.port.in.CriarPagamentoInputPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.application.query.PagamentoResult;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarPagamentoUseCase implements CriarPagamentoInputPort {

    private final PagamentoRepositoryPort pagamentoRepository;

    @Override
    public PagamentoResult execute(CriarPagamentoCommand command) {
        var metodo = MetodoPagamento.valueOf(command.metodoPagamento().toUpperCase());
        var pagamento = Pagamento.criar(command.ordemServicoId(), command.valor(), metodo);
        return PagamentoResultMapper.toResult(pagamentoRepository.salvar(pagamento));
    }
}
