package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.CriarOrdemServicoRequest;
import br.com.oficina.application.dto.OrcamentoResponse;
import br.com.oficina.application.dto.OrdemServicoResponse;
import br.com.oficina.application.service.OrdemDeServicoService;
import br.com.oficina.domain.ordemservico.StatusOS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
@Tag(name = "Ordens de Servico", description = "Fluxo completo da OS. Rastreamento publico (sem autenticacao): GET /api/ordens-servico/numero/{numero}/status")
public class OrdemDeServicoController {

    private final OrdemDeServicoService osService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Criar nova ordem de servico")
    public ResponseEntity<OrdemServicoResponse> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(osService.criarOS(request));
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Buscar OS por ID")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(
            @Parameter(description = "ID interno da OS (campo 'id' retornado na criacao)") @PathVariable Long id) {
        return ResponseEntity.ok(osService.buscarPorId(id));
    }

    @GetMapping("/numero/{numero}")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Buscar OS pelo numero (ex: OS-2026-00001)")
    public ResponseEntity<OrdemServicoResponse> buscarPorNumero(
            @Parameter(description = "Numero legivel da OS (ex: OS-2026-00001)") @PathVariable String numero) {
        return ResponseEntity.ok(osService.buscarPorNumero(numero));
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Listar todas as ordens de servico")
    public ResponseEntity<List<OrdemServicoResponse>> listarTodas() {
        return ResponseEntity.ok(osService.listarTodas());
    }

    @GetMapping("/cliente/{clienteId:\\d+}")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Listar OS de um cliente (uso interno - requer autenticacao)")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorCliente(
            @Parameter(description = "ID interno do cliente") @PathVariable Long clienteId) {
        return ResponseEntity.ok(osService.listarPorCliente(clienteId));
    }

    @GetMapping("/{id:\\d+}/status")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Consultar status de uma OS por ID interno (autenticado)")
    public ResponseEntity<Map<String, String>> consultarStatus(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        String status = osService.consultarStatus(id);                
        return ResponseEntity.ok(Map.of("status", status));           
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Listar OS por status")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorStatus(@PathVariable StatusOS status) {
        return ResponseEntity.ok(osService.listarPorStatus(status));
    }

    // -- fluxo de status --
    @PatchMapping("/{id:\\d+}/iniciar-diagnostico")
    @PreAuthorize("hasAnyRole('MECANICO','GESTOR')")
    @Operation(summary = "Mecanico inicia o diagnostico do veiculo (RECEBIDA -> EM_DIAGNOSTICO)",
               description = "O nome do mecanico e capturado automaticamente do token JWT. Nao requer body.")
    public ResponseEntity<OrdemServicoResponse> iniciarDiagnostico(
            @Parameter(description = "ID interno da OS (campo 'id' retornado na criacao)") @PathVariable Long id) {
        return ResponseEntity.ok(osService.iniciarDiagnostico(id));
    }

    @PostMapping("/{id:\\d+}/orcamento")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Gerar orcamento da OS (requer status EM_DIAGNOSTICO ou EM_EXECUCAO)")
    public ResponseEntity<OrcamentoResponse> gerarOrcamento(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(osService.gerarOrcamento(id));
    }

    @PatchMapping("/{id:\\d+}/aprovar")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Cliente aprova o orcamento")
    public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(osService.aprovarOrcamento(id));
    }

    @PatchMapping("/{id:\\d+}/rejeitar")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Cliente rejeita o orcamento")
    public ResponseEntity<OrdemServicoResponse> rejeitarOrcamento(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(osService.rejeitarOrcamento(id));
    }

    @PatchMapping("/{id:\\d+}/finalizar")
    @PreAuthorize("hasAnyRole('MECANICO','GESTOR')")
    @Operation(summary = "Mecanico finaliza o servico")
    public ResponseEntity<OrdemServicoResponse> finalizarServico(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(osService.finalizarServico(id));
    }

    @PostMapping("/{id:\\d+}/itens")
    @PreAuthorize("hasAnyRole('MECANICO','GESTOR')")
    @Operation(summary = "Adicionar itens a OS (RECEBIDA, EM_DIAGNOSTICO ou EM_EXECUCAO para novo problema)")
    public ResponseEntity<OrdemServicoResponse> adicionarItens(
            @Parameter(description = "ID interno da OS") @PathVariable Long id,
            @Valid @RequestBody java.util.List<br.com.oficina.application.dto.ItemOSRequest> itens) {
        return ResponseEntity.ok(osService.adicionarItens(id, itens));
    }

    @PostMapping("/{id:\\d+}/pagamento")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Registrar pagamento (FINALIZADA -> AGUARDANDO_RETIRADA)")
    public ResponseEntity<OrdemServicoResponse> registrarPagamento(
            @Parameter(description = "ID interno da OS") @PathVariable Long id,
            @Parameter(description = "Metodo de pagamento: PIX, CARTAO_CREDITO, CARTAO_DEBITO, DINHEIRO") @RequestParam String metodoPagamento) {
        return ResponseEntity.ok(osService.registrarPagamento(id, metodoPagamento));
    }

    @PatchMapping("/{id:\\d+}/entregar")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Entregar veiculo ao cliente (AGUARDANDO_RETIRADA -> ENTREGUE)")
    public ResponseEntity<OrdemServicoResponse> entregarVeiculo(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(osService.entregarVeiculo(id));
    }

    // -- tracking público --

    @GetMapping("/numero/{numero}/status")
    @Operation(summary = "Consultar status da OS pelo numero legivel (publico, sem autenticacao)")
    public ResponseEntity<Map<String, Object>> consultarStatusPorNumero(
            @Parameter(description = "Numero legivel da OS (ex: OS-2026-00001)") @PathVariable String numero) {
        OrdemServicoResponse os = osService.buscarPorNumero(numero);
        var resp = new java.util.LinkedHashMap<String, Object>();
        resp.put("numero", os.numero());
        resp.put("status", os.status());
        resp.put("mecanicoNome", os.mecanicoNome());
        return ResponseEntity.ok(resp);
    }

    // -- métricas --

    @GetMapping("/metricas/tempo-medio")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(
        summary = "Tempo medio agregado de todas as OS entregues (GESTOR)",
        description = "Retorna medias globais calculadas sobre todas as OS com status ENTREGUE.\n\n" +
                      "**tempoMedioExecucao**: media de (dataCriacao -> dataFinalizacao) - tempo do trabalho mecanico.\n\n" +
                      "**tempoMedioAtendimento**: media de (dataCriacao -> dataEntrega) - ciclo completo (abertura ate retirada).\n\n" +
                      "Para ver o breakdown de uma OS especifica, use `GET /api/ordens-servico/{id}/metricas`."
    )
    public ResponseEntity<Map<String, Object>> tempoMedioExecucao() {
        long execucao    = Math.round(osService.calcularTempoMedioExecucao());
        long atendimento = Math.round(osService.calcularTempoMedioAtendimento());
        var resp = new java.util.LinkedHashMap<String, Object>();
        resp.put("tempoMedioExecucao",    execucao    > 0 ? formatarDuracao(execucao)    : "sem dados");
        resp.put("tempoMedioAtendimento", atendimento > 0 ? formatarDuracao(atendimento) : "sem dados");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id:\\d+}/metricas")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(
        summary = "Breakdown temporal de uma OS especifica (GESTOR)",
        description = "Retorna as datas e tempos calculados para uma OS:\n\n" +
                      "- **dataCriacao** -> abertura da OS\n" +
                      "- **dataFinalizacao** -> trabalho mecanico concluido (FINALIZADA)\n" +
                      "- **dataEntrega** -> veiculo retirado pelo cliente (ENTREGUE)\n" +
                      "- **tempoExecucao** = dataFinalizacao - dataCriacao (ex: \"2h 30min\", \"1d 4h\")\n" +
                      "- **tempoAtendimento** = dataEntrega - dataCriacao (ciclo completo)\n\n" +
                      "Campos nulos indicam que a OS ainda nao chegou nessa etapa."
    )
    public ResponseEntity<Map<String, Object>> metricasOS(
            @Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(osService.calcularMetricasOS(id));
    }

    private static String formatarDuracao(long minutos) {
        if (minutos <= 0) return "0min";
        long dias  = minutos / (60 * 24);
        long horas = (minutos % (60 * 24)) / 60;
        long min   = minutos % 60;
        var sb = new StringBuilder();
        if (dias  > 0) sb.append(dias).append("d ");
        if (horas > 0) sb.append(horas).append("h ");
        if (min   > 0) sb.append(min).append("min");
        return sb.toString().trim();
    }
}
