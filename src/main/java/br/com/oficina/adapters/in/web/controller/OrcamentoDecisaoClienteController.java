package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.response.DecisaoOrcamentoClienteResponse;
import br.com.oficina.application.command.DecidirOrcamentoPorTokenCommand;
import br.com.oficina.application.port.in.AprovarOrcamentoPorTokenInputPort;
import br.com.oficina.application.port.in.RecusarOrcamentoPorTokenInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orcamentos/decisoes-cliente")
@RequiredArgsConstructor
@Tag(name = "Decisao Externa de Orcamento", description = "Aprovacao ou recusa de orcamento pelo cliente via token.")
public class OrcamentoDecisaoClienteController {

    private final AprovarOrcamentoPorTokenInputPort aprovarOrcamentoPorTokenInputPort;
    private final RecusarOrcamentoPorTokenInputPort recusarOrcamentoPorTokenInputPort;

    @PostMapping("/{token}/aprovar")
    @SecurityRequirements
    @Operation(summary = "Cliente aprova orcamento por token")
    public ResponseEntity<DecisaoOrcamentoClienteResponse> aprovar(@PathVariable String token) {
        var result = aprovarOrcamentoPorTokenInputPort.execute(new DecidirOrcamentoPorTokenCommand(token));
        return ResponseEntity.ok(new DecisaoOrcamentoClienteResponse(
                result.ordemServicoId(),
                result.numeroOrdemServico(),
                result.statusOrdemServico(),
                result.decisao(),
                result.mensagem()));
    }

    @PostMapping("/{token}/recusar")
    @SecurityRequirements
    @Operation(summary = "Cliente recusa orcamento por token")
    public ResponseEntity<DecisaoOrcamentoClienteResponse> recusar(@PathVariable String token) {
        var result = recusarOrcamentoPorTokenInputPort.executeRecusar(new DecidirOrcamentoPorTokenCommand(token));
        return ResponseEntity.ok(new DecisaoOrcamentoClienteResponse(
                result.ordemServicoId(),
                result.numeroOrdemServico(),
                result.statusOrdemServico(),
                result.decisao(),
                result.mensagem()));
    }
}
