package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.EntregaJpaEntity;
import br.com.oficina.domain.model.Entrega;
import org.springframework.stereotype.Component;

@Component
public class EntregaPersistenceMapper {

    public EntregaJpaEntity toEntity(Entrega entrega) {
        return new EntregaJpaEntity(
                entrega.getId(),
                entrega.getOrdemDeServicoId(),
                entrega.getStatus(),
                entrega.getDataEntrega(),
                entrega.getObservacoes());
    }

    public Entrega toDomain(EntregaJpaEntity entity) {
        return Entrega.builder()
                .id(entity.getId())
                .ordemDeServicoId(entity.getOrdemDeServicoId())
                .status(entity.getStatus())
                .dataEntrega(entity.getDataEntrega())
                .observacoes(entity.getObservacoes())
                .build();
    }
}
