package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.PagamentoJpaEntity;
import br.com.oficina.domain.model.Pagamento;
import org.springframework.stereotype.Component;

@Component
public class PagamentoPersistenceMapper {

    public PagamentoJpaEntity toEntity(Pagamento pagamento) {
        return new PagamentoJpaEntity(
                pagamento.getId(),
                pagamento.getOrdemDeServicoId(),
                pagamento.getStatus(),
                pagamento.getMetodo(),
                pagamento.getValor(),
                pagamento.getDataPagamento(),
                pagamento.getTransactionId(),
                pagamento.getGatewayMensagem());
    }

    public Pagamento toDomain(PagamentoJpaEntity entity) {
        return Pagamento.builder()
                .id(entity.getId())
                .ordemDeServicoId(entity.getOrdemDeServicoId())
                .status(entity.getStatus())
                .metodo(entity.getMetodo())
                .valor(entity.getValor())
                .dataPagamento(entity.getDataPagamento())
                .transactionId(entity.getTransactionId())
                .gatewayMensagem(entity.getGatewayMensagem())
                .build();
    }
}
