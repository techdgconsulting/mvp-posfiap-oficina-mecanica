package br.com.oficina.adapters.out.persistence.mapper;

import br.com.oficina.adapters.out.persistence.jpa.ClienteJpaEntity;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.valueobject.CpfCnpj;
import org.springframework.stereotype.Component;

@Component
public class ClientePersistenceMapper {

    public ClienteJpaEntity toEntity(Cliente cliente) {
        return new ClienteJpaEntity(
            cliente.getId(),
            cliente.getDocumento().getValor(),
            cliente.getDocumento().getTipo().name(),
            cliente.getNome(),
            cliente.getTelefone(),
            cliente.getEmail(),
            cliente.getCep(),
            cliente.getLogradouro(),
            cliente.getBairro(),
            cliente.getCidade(),
            cliente.getUf()
        );
    }

    public Cliente toDomain(ClienteJpaEntity entity) {
        return Cliente.builder()
            .id(entity.getId())
            .documento(new CpfCnpj(entity.getDocumento()))
            .nome(entity.getNome())
            .telefone(entity.getTelefone())
            .email(entity.getEmail())
            .cep(entity.getCep())
            .logradouro(entity.getLogradouro())
            .bairro(entity.getBairro())
            .cidade(entity.getCidade())
            .uf(entity.getUf())
            .build();
    }
}
