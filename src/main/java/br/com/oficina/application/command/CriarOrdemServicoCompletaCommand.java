package br.com.oficina.application.command;

import java.util.List;

public record CriarOrdemServicoCompletaCommand(
    String documento,
    String nome,
    String telefone,
    String email,
    String cep,
    String logradouro,
    String bairro,
    String cidade,
    String uf,
    String placa,
    String marca,
    String modelo,
    int ano,
    List<ItemOSCommand> itens,
    String atendenteNome
) {}
