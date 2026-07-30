package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.VeiculoJpaEntity;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.Placa;
import org.springframework.stereotype.Component;

@Component
public class VeiculoPersistenceMapper {

    public VeiculoJpaEntity toEntity(Veiculo veiculo) {
        return new VeiculoJpaEntity(
            veiculo.getId(),
            veiculo.getPlaca().getValor(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getAno(),
            veiculo.getClienteId()
        );
    }

    public Veiculo toDomain(VeiculoJpaEntity entity) {
        return Veiculo.builder()
            .id(entity.getId())
            .placa(new Placa(entity.getPlaca()))
            .marca(entity.getMarca())
            .modelo(entity.getModelo())
            .ano(entity.getAno())
            .clienteId(entity.getClienteId())
            .build();
    }
}
