package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.OrdemDeServicoWebMapper;
import br.com.oficina.adapters.in.web.request.CriarOrdemServicoCompletaRequest;
import br.com.oficina.adapters.in.web.request.CriarOrdemServicoRequest;
import br.com.oficina.adapters.in.web.request.ItemOSRequest;
import br.com.oficina.adapters.in.web.response.NotificacaoOrcamentoResponse;
import br.com.oficina.adapters.in.web.response.OrcamentoResponse;
import br.com.oficina.adapters.in.web.response.OrdemServicoResponse;
import br.com.oficina.application.command.AprovarOrcamentoCommand;
import br.com.oficina.application.command.EntregarVeiculoCommand;
import br.com.oficina.application.command.EnviarNotificacaoOrcamentoCommand;
import br.com.oficina.application.command.FinalizarServicoCommand;
import br.com.oficina.application.command.GerarOrcamentoCommand;
import br.com.oficina.application.command.IniciarDiagnosticoCommand;
import br.com.oficina.application.command.RegistrarPagamentoCommand;
import br.com.oficina.application.command.RejeitarOrcamentoCommand;
import br.com.oficina.application.port.in.AdicionarItensOrdemServicoInputPort;
import br.com.oficina.application.port.in.AprovarOrcamentoInputPort;
import br.com.oficina.application.port.in.BuscarOrdemServicoPorIdInputPort;
import br.com.oficina.application.port.in.BuscarOrdemServicoPorNumeroInputPort;
import br.com.oficina.application.port.in.CalcularMetricasOSInputPort;
import br.com.oficina.application.port.in.CalcularTempoMedioOSInputPort;
import br.com.oficina.application.port.in.ConsultarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.in.CriarOrdemServicoCompletaInputPort;
import br.com.oficina.application.port.in.CriarOrdemServicoInputPort;
import br.com.oficina.application.port.in.EntregarVeiculoInputPort;
import br.com.oficina.application.port.in.EnviarNotificacaoOrcamentoInputPort;
import br.com.oficina.application.port.in.FinalizarServicoInputPort;
import br.com.oficina.application.port.in.GerarOrcamentoInputPort;
import br.com.oficina.application.port.in.IniciarDiagnosticoInputPort;
import br.com.oficina.application.port.in.ListarFilaOrdensServicoInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoPorClienteInputPort;
import br.com.oficina.application.port.in.ListarOrdensServicoPorStatusInputPort;
import br.com.oficina.application.port.in.RegistrarPagamentoInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoInputPort;
import br.com.oficina.domain.valueobject.StatusOS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ordens-servico")
@RequiredArgsConstructor
@Tag(name = "Ordens de Servico", description = "Gestao completa da OS.")
public class OrdemDeServicoController {

    private final CriarOrdemServicoInputPort criarOrdemServicoInputPort;
    private final CriarOrdemServicoCompletaInputPort criarOrdemServicoCompletaInputPort;
    private final BuscarOrdemServicoPorIdInputPort buscarOrdemServicoPorIdInputPort;
    private final BuscarOrdemServicoPorNumeroInputPort buscarOrdemServicoPorNumeroInputPort;
    private final ListarOrdensServicoInputPort listarOrdensServicoInputPort;
    private final ListarFilaOrdensServicoInputPort listarFilaOrdensServicoInputPort;
    private final ListarOrdensServicoPorClienteInputPort listarOrdensServicoPorClienteInputPort;
    private final ListarOrdensServicoPorStatusInputPort listarOrdensServicoPorStatusInputPort;
    private final ConsultarStatusOrdemServicoInputPort consultarStatusOrdemServicoInputPort;
    private final IniciarDiagnosticoInputPort iniciarDiagnosticoInputPort;
    private final GerarOrcamentoInputPort gerarOrcamentoInputPort;
    private final EnviarNotificacaoOrcamentoInputPort enviarNotificacaoOrcamentoInputPort;
    private final AprovarOrcamentoInputPort aprovarOrcamentoInputPort;
    private final RejeitarOrcamentoInputPort rejeitarOrcamentoInputPort;
    private final FinalizarServicoInputPort finalizarServicoInputPort;
    private final RegistrarPagamentoInputPort registrarPagamentoInputPort;
    private final EntregarVeiculoInputPort entregarVeiculoInputPort;
    private final AdicionarItensOrdemServicoInputPort adicionarItensOrdemServicoInputPort;
    private final CalcularMetricasOSInputPort calcularMetricasOSInputPort;
    private final CalcularTempoMedioOSInputPort calcularTempoMedioOSInputPort;
    private final OrdemDeServicoWebMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Criar nova ordem de servico")
    public ResponseEntity<OrdemServicoResponse> criar(
            @Valid @RequestBody CriarOrdemServicoRequest request,
            Principal principal) {
        var result = criarOrdemServicoInputPort.execute(mapper.toCriarCommand(request, nome(principal)));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @PostMapping("/completa")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Criar ordem de servico completa com dados de cliente e veiculo")
    public ResponseEntity<OrdemServicoResponse> criarCompleta(
            @Valid @RequestBody CriarOrdemServicoCompletaRequest request,
            Principal principal) {
        var result = criarOrdemServicoCompletaInputPort.execute(mapper.toCriarCompletaCommand(request, nome(principal)));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Buscar OS por ID")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@Parameter(description = "ID interno da OS") @PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(buscarOrdemServicoPorIdInputPort.execute(id)));
    }

    @GetMapping("/numero/{numero}")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Buscar OS pelo numero")
    public ResponseEntity<OrdemServicoResponse> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(mapper.toResponse(buscarOrdemServicoPorNumeroInputPort.execute(numero)));
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Listar todas as ordens de servico")
    public ResponseEntity<List<OrdemServicoResponse>> listarTodas() {
        return ResponseEntity.ok(listarOrdensServicoInputPort.execute().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/fila")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Listar fila operacional de ordens de servico")
    public ResponseEntity<List<OrdemServicoResponse>> listarFilaOperacional() {
        return ResponseEntity.ok(listarFilaOrdensServicoInputPort.listarFilaOperacional().stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/cliente/{clienteId:\\d+}")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Listar OS de um cliente")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(listarOrdensServicoPorClienteInputPort.executeByCliente(clienteId).stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id:\\d+}/status")
    @PreAuthorize("hasAnyRole('ATENDENTE','MECANICO','GESTOR')")
    @Operation(summary = "Consultar status de uma OS por ID")
    public ResponseEntity<Map<String, String>> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("status", consultarStatusOrdemServicoInputPort.executeStatus(id)));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Listar OS por status")
    public ResponseEntity<List<OrdemServicoResponse>> listarPorStatus(@PathVariable StatusOS status) {
        return ResponseEntity.ok(listarOrdensServicoPorStatusInputPort.execute(status).stream().map(mapper::toResponse).toList());
    }

    @PatchMapping("/{id:\\d+}/iniciar-diagnostico")
    @PreAuthorize("hasAnyRole('MECANICO','GESTOR')")
    @Operation(summary = "Mecanico inicia o diagnostico do veiculo")
    public ResponseEntity<OrdemServicoResponse> iniciarDiagnostico(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(mapper.toResponse(iniciarDiagnosticoInputPort.execute(new IniciarDiagnosticoCommand(id, nome(principal)))));
    }

    @PostMapping("/{id:\\d+}/orcamento")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Gerar orcamento da OS")
    public ResponseEntity<OrcamentoResponse> gerarOrcamento(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(gerarOrcamentoInputPort.execute(new GerarOrcamentoCommand(id))));
    }

    @PostMapping("/{id:\\d+}/orcamento/notificar-cliente")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Enviar notificacao de orcamento ao cliente")
    public ResponseEntity<NotificacaoOrcamentoResponse> notificarClienteOrcamento(@PathVariable Long id) {
        var result = enviarNotificacaoOrcamentoInputPort.execute(new EnviarNotificacaoOrcamentoCommand(id));
        return ResponseEntity.ok(new NotificacaoOrcamentoResponse(
                result.orcamentoId(),
                result.ordemServicoId(),
                result.numeroOrdemServico(),
                result.emailDestino(),
                result.dataExpiracao(),
                result.linkAprovacao(),
                result.linkRecusa()));
    }

    @PatchMapping("/{id:\\d+}/aprovar")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Cliente aprova o orcamento")
    public ResponseEntity<OrdemServicoResponse> aprovarOrcamento(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(aprovarOrcamentoInputPort.execute(new AprovarOrcamentoCommand(id))));
    }

    @PatchMapping("/{id:\\d+}/rejeitar")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Cliente rejeita o orcamento")
    public ResponseEntity<OrdemServicoResponse> rejeitarOrcamento(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(rejeitarOrcamentoInputPort.execute(new RejeitarOrcamentoCommand(id))));
    }

    @PatchMapping("/{id:\\d+}/finalizar")
    @PreAuthorize("hasAnyRole('MECANICO','GESTOR')")
    @Operation(summary = "Mecanico finaliza o servico")
    public ResponseEntity<OrdemServicoResponse> finalizarServico(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(finalizarServicoInputPort.execute(new FinalizarServicoCommand(id))));
    }

    @PostMapping("/{id:\\d+}/itens")
    @PreAuthorize("hasAnyRole('MECANICO','GESTOR')")
    @Operation(summary = "Adicionar itens a OS")
    public ResponseEntity<OrdemServicoResponse> adicionarItens(
            @PathVariable Long id,
            @Valid @RequestBody List<ItemOSRequest> itens) {
        return ResponseEntity.ok(mapper.toResponse(adicionarItensOrdemServicoInputPort.execute(mapper.toAdicionarItensCommand(id, itens))));
    }

    @PostMapping("/{id:\\d+}/pagamento")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Registrar pagamento")
    public ResponseEntity<OrdemServicoResponse> registrarPagamento(
            @PathVariable Long id,
            @RequestParam String metodoPagamento) {
        return ResponseEntity.ok(mapper.toResponse(registrarPagamentoInputPort.execute(new RegistrarPagamentoCommand(id, metodoPagamento))));
    }

    @PatchMapping("/{id:\\d+}/entregar")
    @PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
    @Operation(summary = "Entregar veiculo ao cliente")
    public ResponseEntity<OrdemServicoResponse> entregarVeiculo(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(entregarVeiculoInputPort.execute(new EntregarVeiculoCommand(id))));
    }

    @GetMapping("/numero/{numero}/status")
    @Operation(summary = "Consultar status da OS pelo numero legivel")
    @SecurityRequirements
    public ResponseEntity<Map<String, Object>> consultarStatusPorNumero(@PathVariable String numero) {
        var os = buscarOrdemServicoPorNumeroInputPort.execute(numero);
        var resp = new LinkedHashMap<String, Object>();
        resp.put("numero", os.numero());
        resp.put("status", os.status());
        resp.put("mecanicoNome", os.mecanicoNome());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/metricas/tempo-medio")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Tempo medio agregado de todas as OS entregues")
    public ResponseEntity<Map<String, Object>> tempoMedioExecucao() {
        var result = calcularTempoMedioOSInputPort.executeTempoMedio();
        long execucao = Math.round(result.tempoMedioExecucao());
        long atendimento = Math.round(result.tempoMedioAtendimento());
        var resp = new LinkedHashMap<String, Object>();
        resp.put("tempoMedioExecucao", execucao > 0 ? formatarDuracao(execucao) : "sem dados");
        resp.put("tempoMedioAtendimento", atendimento > 0 ? formatarDuracao(atendimento) : "sem dados");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id:\\d+}/metricas")
    @PreAuthorize("hasRole('GESTOR')")
    @Operation(summary = "Breakdown temporal de uma OS especifica")
    public ResponseEntity<Map<String, Object>> metricasOS(@PathVariable Long id) {
        var metricas = calcularMetricasOSInputPort.executeMetricas(id);
        var resp = new LinkedHashMap<String, Object>();
        resp.put("numero", metricas.numero());
        resp.put("status", metricas.status());
        resp.put("dataCriacao", metricas.dataCriacao());
        resp.put("dataFinalizacao", metricas.dataFinalizacao());
        resp.put("tempoExecucao", metricas.tempoExecucao());
        resp.put("dataEntrega", metricas.dataEntrega());
        resp.put("tempoAtendimento", metricas.tempoAtendimento());
        return ResponseEntity.ok(resp);
    }

    private static String nome(Principal principal) {
        return principal != null ? principal.getName() : "desconhecido";
    }

    private static String formatarDuracao(long minutos) {
        if (minutos <= 0) return "0min";
        long dias = minutos / (60 * 24);
        long horas = (minutos % (60 * 24)) / 60;
        long min = minutos % 60;
        var sb = new StringBuilder();
        if (dias > 0) sb.append(dias).append("d ");
        if (horas > 0) sb.append(horas).append("h ");
        if (min > 0) sb.append(min).append("min");
        return sb.toString().trim();
    }
}
