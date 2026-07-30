package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.CpfCnpj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Cliente {

    private Long id;
    private CpfCnpj documento;
    private String nome;
    private String telefone;
    private String email;
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;

    public Cliente(CpfCnpj documento, String nome, String telefone, String email) {
        if (documento == null) {
            throw new IllegalArgumentException("Documento validado e obrigatorio");
        }
        this.documento = documento;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

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
