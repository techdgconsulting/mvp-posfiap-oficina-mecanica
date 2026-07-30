package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.VeiculoWebMapper;
import br.com.oficina.adapters.in.web.request.VeiculoRequest;
import br.com.oficina.adapters.in.web.response.VeiculoResponse;
import br.com.oficina.application.port.in.AtualizarVeiculoInputPort;
import br.com.oficina.application.port.in.BuscarVeiculoPorIdInputPort;
import br.com.oficina.application.port.in.CriarVeiculoInputPort;
import br.com.oficina.application.port.in.ExcluirVeiculoInputPort;
import br.com.oficina.application.port.in.ListarVeiculosInputPort;
import br.com.oficina.application.port.in.ListarVeiculosPorClienteInputPort;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
@Tag(name = "Veiculos")
public class VeiculoController {

    private final CriarVeiculoInputPort criarVeiculoInputPort;
    private final AtualizarVeiculoInputPort atualizarVeiculoInputPort;
    private final ExcluirVeiculoInputPort excluirVeiculoInputPort;
    private final BuscarVeiculoPorIdInputPort buscarVeiculoPorIdInputPort;
    private final ListarVeiculosInputPort listarVeiculosInputPort;
    private final ListarVeiculosPorClienteInputPort listarVeiculosPorClienteInputPort;
    private final VeiculoWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar veiculo")
    public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest request) {
        var response = criarVeiculoInputPort.execute(mapper.toCriarCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(response));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Buscar veiculo por ID")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(buscarVeiculoPorIdInputPort.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todos os veiculos")
    public ResponseEntity<List<VeiculoResponse>> listarTodos() {
        return ResponseEntity.ok(listarVeiculosInputPort.execute().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/cliente/{clienteId:\\d+}")
    @Operation(summary = "Listar veiculos de um cliente")
    public ResponseEntity<List<VeiculoResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(listarVeiculosPorClienteInputPort.execute(clienteId).stream().map(mapper::toResponse).toList());
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Atualizar veiculo")
    public ResponseEntity<VeiculoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.ok(mapper.toResponse(atualizarVeiculoInputPort.execute(mapper.toAtualizarCommand(id, request))));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Excluir veiculo")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirVeiculoInputPort.execute(mapper.toExcluirCommand(id));
        return ResponseEntity.noContent().build();
    }
}
