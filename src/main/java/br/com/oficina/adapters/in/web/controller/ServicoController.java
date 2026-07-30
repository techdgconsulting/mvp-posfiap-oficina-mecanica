package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.ServicoWebMapper;
import br.com.oficina.adapters.in.web.request.ServicoRequest;
import br.com.oficina.adapters.in.web.response.ServicoResponse;
import br.com.oficina.application.command.ExcluirServicoCommand;
import br.com.oficina.application.port.in.AtualizarServicoInputPort;
import br.com.oficina.application.port.in.BuscarServicoPorIdInputPort;
import br.com.oficina.application.port.in.CriarServicoInputPort;
import br.com.oficina.application.port.in.ExcluirServicoInputPort;
import br.com.oficina.application.port.in.ListarServicosInputPort;
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
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GESTOR')")
@Tag(name = "Servicos", description = "Catalogo de servicos oferecidos")
public class ServicoController {

    private final CriarServicoInputPort criarServicoInputPort;
    private final BuscarServicoPorIdInputPort buscarServicoPorIdInputPort;
    private final ListarServicosInputPort listarServicosInputPort;
    private final AtualizarServicoInputPort atualizarServicoInputPort;
    private final ExcluirServicoInputPort excluirServicoInputPort;
    private final ServicoWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar novo servico")
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(criarServicoInputPort.execute(mapper.toCriarCommand(request))));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Buscar servico por ID")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(buscarServicoPorIdInputPort.execute(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todos os servicos")
    public ResponseEntity<List<ServicoResponse>> listarTodos() {
        return ResponseEntity.ok(listarServicosInputPort.execute().stream().map(mapper::toResponse).toList());
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Atualizar servico")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.ok(mapper.toResponse(atualizarServicoInputPort.execute(mapper.toAtualizarCommand(id, request))));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Excluir servico")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirServicoInputPort.execute(new ExcluirServicoCommand(id));
        return ResponseEntity.noContent().build();
    }
}
