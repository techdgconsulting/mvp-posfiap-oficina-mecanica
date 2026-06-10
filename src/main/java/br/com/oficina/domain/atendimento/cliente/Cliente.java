package br.com.oficina.domain.atendimento.cliente;

import br.com.oficina.domain.atendimento.cliente.vo.CpfCnpj;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CpfCnpj documento;

    @Column(nullable = false)
    private String nome;

    private String telefone;

    private String email;

    private String cep;

    private String logradouro;

    private String bairro;

    private String cidade;

    private String uf;

    public void atualizar(String nome, String telefone, String email) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public void preencherEndereco(String cep, String logradouro, String bairro, String cidade, String uf) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }
}
