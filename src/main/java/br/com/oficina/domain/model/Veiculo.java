package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.Placa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Veiculo {

    private Long id;
    private Placa placa;
    private String marca;
    private String modelo;
    private int ano;
    private Long clienteId;
    private Cliente cliente;

    public Veiculo(Placa placa, String marca, String modelo, int ano, Cliente cliente) {
        if (placa == null) {
            throw new IllegalArgumentException("Placa validada e obrigatoria");
        }
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente e obrigatorio para criar um veiculo");
        }
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cliente = cliente;
        this.clienteId = cliente.getId();
    }

    public void atualizar(String marca, String modelo, int ano) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
    }
}
