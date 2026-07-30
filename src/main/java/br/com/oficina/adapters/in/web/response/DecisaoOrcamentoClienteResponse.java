package br.com.oficina.adapters.in.web.response;

public record DecisaoOrcamentoClienteResponse(
        Long ordemServicoId,
        String numeroOrdemServico,
        String statusOrdemServico,
        String decisao,
        String mensagem) {}
