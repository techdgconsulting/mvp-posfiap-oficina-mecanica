package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.PecaRequest;
import br.com.oficina.application.dto.PecaResponse;
import br.com.oficina.application.service.PecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GESTOR')")
@Tag(name = "Peças e Insumos", description = "CRUD com controle de estoque")
public class PecaController {

    private final PecaService pecaService;

    @PostMapping
    @Operation(summary = "Cadastrar nova peça")
    public ResponseEntity<PecaResponse> criar(@Valid @RequestBody PecaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.criar(request));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Buscar peça por ID")
    public ResponseEntity<PecaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pecaService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas as peças")
    public ResponseEntity<List<PecaResponse>> listarTodas() {
        return ResponseEntity.ok(pecaService.listarTodas());
    }

    @GetMapping("/estoque-baixo")
    @Operation(summary = "Listar peças com estoque abaixo do mínimo configurado por peça")
    public ResponseEntity<List<PecaResponse>> estoqueBaixo() {
        return ResponseEntity.ok(pecaService.listarEstoqueBaixo());
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Atualizar peça")
    public ResponseEntity<PecaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PecaRequest request) {
        return ResponseEntity.ok(pecaService.atualizar(id, request));
    }

    @PatchMapping("/{id:\\d+}/repor-estoque")
    @Operation(summary = "Repor estoque de uma peça")
    public ResponseEntity<PecaResponse> reporEstoque(@PathVariable Long id, @RequestParam int quantidade) {
        return ResponseEntity.ok(pecaService.reporEstoque(id, quantidade));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Excluir peça")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pecaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
