package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.PecaWebMapper;
import br.com.oficina.adapters.in.web.request.PecaRequest;
import br.com.oficina.adapters.in.web.response.DisponibilidadePecaResponse;
import br.com.oficina.adapters.in.web.response.PecaResponse;
import br.com.oficina.application.command.BaixarEstoqueCommand;
import br.com.oficina.application.command.ExcluirPecaCommand;
import br.com.oficina.application.command.ReporEstoqueCommand;
import br.com.oficina.application.command.VerificarDisponibilidadePecaCommand;
import br.com.oficina.application.port.in.AtualizarPecaInputPort;
import br.com.oficina.application.port.in.BaixarEstoqueInputPort;
import br.com.oficina.application.port.in.BuscarPecaPorIdInputPort;
import br.com.oficina.application.port.in.CriarPecaInputPort;
import br.com.oficina.application.port.in.ExcluirPecaInputPort;
import br.com.oficina.application.port.in.ListarPecasComEstoqueBaixoInputPort;
import br.com.oficina.application.port.in.ListarPecasInputPort;
import br.com.oficina.application.port.in.ReporEstoqueInputPort;
import br.com.oficina.application.port.in.VerificarDisponibilidadePecaInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GESTOR')")
@Tag(name = "Pecas e Insumos", description = "CRUD com controle de estoque")
public class PecaController {

    private final CriarPecaInputPort criarPecaInputPort;
    private final BuscarPecaPorIdInputPort buscarPecaPorIdInputPort;
    private final ListarPecasInputPort listarPecasInputPort;
    private final ListarPecasComEstoqueBaixoInputPort listarPecasComEstoqueBaixoInputPort;
    private final AtualizarPecaInputPort atualizarPecaInputPort;
    private final ReporEstoqueInputPort reporEstoqueInputPort;
    private final BaixarEstoqueInputPort baixarEstoqueInputPort;
    private final VerificarDisponibilidadePecaInputPort verificarDisponibilidadePecaInputPort;
    private final ExcluirPecaInputPort excluirPecaInputPort;
    private final PecaWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar nova peca")
    public ResponseEntity<PecaResponse> criar(@Valid @RequestBody PecaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(criarPecaInputPort.execute(mapper.toCriarCommand(request))));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Buscar peca por ID")
    public ResponseEntity<PecaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(buscarPecaPorIdInputPort.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todas as pecas")
    public ResponseEntity<List<PecaResponse>> listarTodas() {
        return ResponseEntity.ok(listarPecasInputPort.execute().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/estoque-baixo")
    @Operation(summary = "Listar pecas com estoque abaixo do minimo configurado por peca")
    public ResponseEntity<List<PecaResponse>> estoqueBaixo() {
        return ResponseEntity.ok(listarPecasComEstoqueBaixoInputPort.executeEstoqueBaixo().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id:\\d+}/disponibilidade")
    @Operation(summary = "Verificar disponibilidade de estoque")
    public ResponseEntity<DisponibilidadePecaResponse> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam int quantidade) {
        return ResponseEntity.ok(mapper.toResponse(
                verificarDisponibilidadePecaInputPort.execute(new VerificarDisponibilidadePecaCommand(id, quantidade))));
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Atualizar peca")
    public ResponseEntity<PecaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PecaRequest request) {
        return ResponseEntity.ok(mapper.toResponse(atualizarPecaInputPort.execute(mapper.toAtualizarCommand(id, request))));
    }

    @PatchMapping("/{id:\\d+}/repor-estoque")
    @Operation(summary = "Repor estoque de uma peca")
    public ResponseEntity<PecaResponse> reporEstoque(@PathVariable Long id, @RequestParam int quantidade) {
        return ResponseEntity.ok(mapper.toResponse(reporEstoqueInputPort.execute(new ReporEstoqueCommand(id, quantidade))));
    }

    @PatchMapping("/{id:\\d+}/baixar-estoque")
    @Operation(summary = "Baixar estoque de uma peca")
    public ResponseEntity<PecaResponse> baixarEstoque(@PathVariable Long id, @RequestParam int quantidade) {
        return ResponseEntity.ok(mapper.toResponse(baixarEstoqueInputPort.execute(new BaixarEstoqueCommand(id, quantidade))));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Excluir peca")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirPecaInputPort.execute(new ExcluirPecaCommand(id));
        return ResponseEntity.noContent().build();
    }
}
