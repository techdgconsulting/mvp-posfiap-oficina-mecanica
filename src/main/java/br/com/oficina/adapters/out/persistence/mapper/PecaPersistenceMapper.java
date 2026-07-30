package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.PecaJpaEntity;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.valueobject.Quantidade;
import org.springframework.stereotype.Component;

@Component
public class PecaPersistenceMapper {

    public PecaJpaEntity toEntity(Peca peca) {
        return new PecaJpaEntity(
                peca.getId(),
                peca.getNome(),
                peca.getDescricao(),
                peca.getQuantidadeEstoqueValor(),
                peca.getValorUnitario(),
                peca.getEstoqueMinimo());
    }

    public Peca toDomain(PecaJpaEntity entity) {
        return Peca.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .quantidadeEstoque(new Quantidade(entity.getQuantidadeEstoque()))
                .valorUnitario(entity.getValorUnitario())
                .estoqueMinimo(entity.getEstoqueMinimo())
                .build();
    }
}
