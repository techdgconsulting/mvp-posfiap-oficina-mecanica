package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.OrcamentoDecisaoClienteJpaEntity;
import br.com.oficina.domain.model.OrcamentoDecisaoCliente;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoDecisaoClientePersistenceMapper {

    public OrcamentoDecisaoClienteJpaEntity toEntity(OrcamentoDecisaoCliente decisao) {
        return new OrcamentoDecisaoClienteJpaEntity(
                decisao.getId(),
                decisao.getOrcamentoId(),
                decisao.getOrdemServicoId(),
                decisao.getTokenHash(),
                decisao.getStatus(),
                decisao.getDataCriacao(),
                decisao.getDataExpiracao(),
                decisao.getDataDecisao(),
                decisao.getEmailDestino());
    }

    public OrcamentoDecisaoCliente toDomain(OrcamentoDecisaoClienteJpaEntity entity) {
        return OrcamentoDecisaoCliente.builder()
                .id(entity.getId())
                .orcamentoId(entity.getOrcamentoId())
                .ordemServicoId(entity.getOrdemServicoId())
                .tokenHash(entity.getTokenHash())
                .status(entity.getStatus())
                .dataCriacao(entity.getDataCriacao())
                .dataExpiracao(entity.getDataExpiracao())
                .dataDecisao(entity.getDataDecisao())
                .emailDestino(entity.getEmailDestino())
                .build();
    }
}
