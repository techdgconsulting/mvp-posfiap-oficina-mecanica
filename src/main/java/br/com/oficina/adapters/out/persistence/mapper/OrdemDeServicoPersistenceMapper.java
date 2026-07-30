package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.ItemOSJpaEntity;
import br.com.oficina.adapters.out.persistence.jpa.OrdemDeServicoJpaEntity;
import br.com.oficina.domain.model.ItemOS;
import br.com.oficina.domain.model.OrdemDeServico;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class OrdemDeServicoPersistenceMapper {

    public OrdemDeServicoJpaEntity toEntity(OrdemDeServico os) {
        var entity = new OrdemDeServicoJpaEntity(
            os.getId(),
            os.getNumero(),
            os.getStatus(),
            os.getDataCriacao(),
            os.getDataFinalizacao(),
            os.getAtendenteNome(),
            os.getClienteId(),
            os.getVeiculoId(),
            new ArrayList<>()
        );
        os.getItens().forEach(item -> entity.adicionarItem(toItemEntity(item)));
        return entity;
    }

    public OrdemDeServico toDomain(OrdemDeServicoJpaEntity entity) {
        var os = OrdemDeServico.builder()
            .id(entity.getId())
            .numero(entity.getNumero())
            .status(entity.getStatus())
            .dataCriacao(entity.getDataCriacao())
            .dataFinalizacao(entity.getDataFinalizacao())
            .atendenteNome(entity.getAtendenteNome())
            .clienteId(entity.getClienteId())
            .veiculoId(entity.getVeiculoId())
            .itens(new ArrayList<>())
            .build();
        entity.getItens().stream().map(this::toItemDomain).forEach(os::adicionarItem);
        return os;
    }

    private ItemOSJpaEntity toItemEntity(ItemOS item) {
        return new ItemOSJpaEntity(
            item.getId(),
            item.getTipo(),
            item.getDescricao(),
            item.getQuantidade(),
            item.getValorUnitario(),
            item.getReferenciaId(),
            item.isEstoqueReduzido(),
            null
        );
    }

    private ItemOS toItemDomain(ItemOSJpaEntity entity) {
        return ItemOS.builder()
            .id(entity.getId())
            .tipo(entity.getTipo())
            .descricao(entity.getDescricao())
            .quantidade(entity.getQuantidade())
            .valorUnitario(entity.getValorUnitario())
            .referenciaId(entity.getReferenciaId())
            .estoqueReduzido(entity.isEstoqueReduzido())
            .ordemDeServicoId(entity.getOrdemDeServico() != null ? entity.getOrdemDeServico().getId() : null)
            .build();
    }
}
