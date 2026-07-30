package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.ServicoJpaEntity;
import br.com.oficina.domain.model.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoPersistenceMapper {

    public ServicoJpaEntity toEntity(Servico servico) {
        return new ServicoJpaEntity(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getValorUnitario(),
                servico.getTempoEstimadoMinutos());
    }

    public Servico toDomain(ServicoJpaEntity entity) {
        return Servico.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .valorUnitario(entity.getValorUnitario())
                .tempoEstimadoMinutos(entity.getTempoEstimadoMinutos())
                .build();
    }
}
