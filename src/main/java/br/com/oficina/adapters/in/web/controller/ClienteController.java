package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.ClienteWebMapper;
import br.com.oficina.adapters.in.web.request.ClienteRequest;
import br.com.oficina.adapters.in.web.response.ClienteResponse;
import br.com.oficina.application.port.in.AtualizarClienteInputPort;
import br.com.oficina.application.port.in.BuscarClientePorDocumentoInputPort;
import br.com.oficina.application.port.in.BuscarClientePorIdInputPort;
import br.com.oficina.application.port.in.CriarClienteInputPort;
import br.com.oficina.application.port.in.ExcluirClienteInputPort;
import br.com.oficina.application.port.in.ListarClientesInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ATENDENTE','GESTOR')")
@Tag(name = "Clientes", description = "Gestao de clientes da oficina")
public class ClienteController {

    private final CriarClienteInputPort criarClienteInputPort;
    private final AtualizarClienteInputPort atualizarClienteInputPort;
    private final ExcluirClienteInputPort excluirClienteInputPort;
    private final BuscarClientePorIdInputPort buscarClientePorIdInputPort;
    private final BuscarClientePorDocumentoInputPort buscarClientePorDocumentoInputPort;
    private final ListarClientesInputPort listarClientesInputPort;
    private final ClienteWebMapper mapper;

    @PostMapping
    @Operation(summary = "Cadastrar novo cliente")
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request, Principal principal) {
        var response = criarClienteInputPort.execute(mapper.toCriarCommand(request, atendenteNome(principal)));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(response));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(buscarClientePorIdInputPort.execute(id)));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Buscar cliente por CPF/CNPJ")
    public ResponseEntity<ClienteResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(mapper.toResponse(buscarClientePorDocumentoInputPort.execute(documento)));
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        return ResponseEntity.ok(listarClientesInputPort.execute().stream().map(mapper::toResponse).toList());
    }

    @PutMapping("/{id:\\d+}")
    @Operation(summary = "Atualizar cliente")
    public ResponseEntity<ClienteResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request,
            Principal principal) {
        var response = atualizarClienteInputPort.execute(mapper.toAtualizarCommand(id, request, atendenteNome(principal)));
        return ResponseEntity.ok(mapper.toResponse(response));
    }

    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "Excluir cliente")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Principal principal) {
        excluirClienteInputPort.execute(mapper.toExcluirCommand(id, atendenteNome(principal)));
        return ResponseEntity.noContent().build();
    }

    private String atendenteNome(Principal principal) {
        return principal != null ? principal.getName() : "desconhecido";
    }
}
