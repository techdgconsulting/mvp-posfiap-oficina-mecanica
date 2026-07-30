package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.EncerramentoJpaEntity;
import br.com.oficina.domain.model.Encerramento;
import org.springframework.stereotype.Component;

@Component
public class EncerramentoPersistenceMapper {

    public EncerramentoJpaEntity toEntity(Encerramento encerramento) {
        return new EncerramentoJpaEntity(
                encerramento.getId(),
                encerramento.getOrdemDeServicoId(),
                encerramento.getStatus(),
                encerramento.getDataEncerramento());
    }

    public Encerramento toDomain(EncerramentoJpaEntity entity) {
        return Encerramento.builder()
                .id(entity.getId())
                .ordemDeServicoId(entity.getOrdemDeServicoId())
                .status(entity.getStatus())
                .dataEncerramento(entity.getDataEncerramento())
                .build();
    }
}
