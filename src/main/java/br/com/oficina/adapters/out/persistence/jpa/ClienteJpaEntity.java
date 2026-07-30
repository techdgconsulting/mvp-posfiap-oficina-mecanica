package br.com.oficina.adapters.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento", nullable = false, unique = true, length = 18)
    private String documento;

    @Column(name = "tipo_documento", nullable = false, length = 4)
    private String tipoDocumento;

    @Column(nullable = false)
    private String nome;

    private String telefone;
    private String email;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
