package br.com.oficina.application.query;

public record DecisaoOrcamentoClienteResult(
        Long ordemServicoId,
        String numeroOrdemServico,
        String statusOrdemServico,
        String decisao,
        String mensagem) {}
