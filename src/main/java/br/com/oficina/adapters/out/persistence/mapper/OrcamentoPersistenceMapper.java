package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.OrcamentoJpaEntity;
import br.com.oficina.domain.model.Orcamento;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoPersistenceMapper {

    public OrcamentoJpaEntity toEntity(Orcamento orcamento) {
        return new OrcamentoJpaEntity(
                orcamento.getId(),
                orcamento.getOrdemDeServicoId(),
                orcamento.getStatus(),
                orcamento.getValorTotal(),
                orcamento.getDataCriacao(),
                orcamento.getDataValidade(),
                orcamento.getDataAprovacao());
    }

    public Orcamento toDomain(OrcamentoJpaEntity entity) {
        return Orcamento.builder()
                .id(entity.getId())
                .ordemDeServicoId(entity.getOrdemDeServicoId())
                .status(entity.getStatus())
                .valorTotal(entity.getValorTotal())
                .dataCriacao(entity.getDataCriacao())
                .dataValidade(entity.getDataValidade())
                .dataAprovacao(entity.getDataAprovacao())
                .build();
    }
}
