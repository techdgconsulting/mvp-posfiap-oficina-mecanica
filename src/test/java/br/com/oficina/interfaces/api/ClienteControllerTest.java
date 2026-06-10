package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.ClienteRequest;
import br.com.oficina.application.dto.ClienteResponse;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.service.ClienteService;
import br.com.oficina.infrastructure.security.JwtService;
import br.com.oficina.infrastructure.security.SecurityConfig;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import br.com.oficina.application.exception.NegocioException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
@Epic("Atendimento ao Cliente")
@Feature("API REST Clientes")
class ClienteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private ClienteService clienteService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;

    private final ClienteResponse resp = new ClienteResponse(1L, "529.982.247-25", "CPF", "João", "11999", "j@e.com",
            "01001-000", "Praça da Sé", "Sé", "São Paulo", "SP");

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar cliente via API")
    void deveCriarCliente() throws Exception {
        when(clienteService.criar(any())).thenReturn(resp);

        var req = new ClienteRequest("52998224725", "João", "11999", "j@e.com", "01001000");
        mockMvc.perform(post("/api/clientes").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("João"))
            .andExpect(jsonPath("$.cep").value("01001-000"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar cliente por ID via API")
    void deveBuscarPorId() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/clientes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar cliente por documento via API")
    void deveBuscarPorDocumento() throws Exception {
        when(clienteService.buscarPorDocumento("52998224725")).thenReturn(resp);

        mockMvc.perform(get("/api/clientes/documento/52998224725"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.documento").value("529.982.247-25"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todos os clientes via API")
    void deveListarTodos() throws Exception {
        when(clienteService.listarTodos()).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar cliente via API")
    void deveAtualizar() throws Exception {
        when(clienteService.atualizar(eq(1L), any())).thenReturn(resp);

        var req = new ClienteRequest("52998224725", "João", "11888", "novo@e.com", null);
        mockMvc.perform(put("/api/clientes/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir cliente via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/clientes/1").with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticação")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar criação de cliente duplicado")
    void criarClienteComDocumentoDuplicado_retorna422() throws Exception {
        when(clienteService.criar(any())).thenThrow(new NegocioException("Já existe um cliente com esse documento"));

        var req = new ClienteRequest("52998224725", "João", "11999", "j@e.com", null);
        mockMvc.perform(post("/api/clientes").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Já existe um cliente com esse documento"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao buscar por documento inexistente")
    void buscarPorDocumentoNaoEncontrado_retorna404() throws Exception {
        when(clienteService.buscarPorDocumento("00000000000"))
            .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado com documento: 00000000000"));

        mockMvc.perform(get("/api/clientes/documento/00000000000"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente não encontrado com documento: 00000000000"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar cliente inexistente")
    void atualizarClienteNaoEncontrado_retorna404() throws Exception {
        when(clienteService.atualizar(eq(99L), any()))
            .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado: 99"));

        var req = new ClienteRequest("52998224725", "João", "11999", "j@e.com", null);
        mockMvc.perform(put("/api/clientes/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente não encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir cliente inexistente")
    void excluirClienteNaoEncontrado_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Cliente não encontrado: 99")).when(clienteService).excluir(99L);

        mockMvc.perform(delete("/api/clientes/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente não encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void excluirClienteComVeiculos_retorna422() throws Exception {
        doThrow(new NegocioException("Não é possível excluir cliente com veículos vinculados")).when(clienteService).excluir(1L);

        mockMvc.perform(delete("/api/clientes/1").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Não é possível excluir cliente com veículos vinculados"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void excluirClienteComOrdensDeServico_retorna422() throws Exception {
        doThrow(new NegocioException("Não é possível excluir cliente com ordens de serviço vinculadas")).when(clienteService).excluir(1L);

        mockMvc.perform(delete("/api/clientes/1").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Não é possível excluir cliente com ordens de serviço vinculadas"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void clienteNaoEncontrado_retorna404() throws Exception {
        when(clienteService.buscarPorId(99L)).thenThrow(new RecursoNaoEncontradoException("Não encontrado"));

        mockMvc.perform(get("/api/clientes/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Não encontrado"));
    }
}

