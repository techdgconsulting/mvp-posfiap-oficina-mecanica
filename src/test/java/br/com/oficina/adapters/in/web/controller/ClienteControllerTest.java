package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.ClienteWebMapper;
import br.com.oficina.adapters.in.web.request.ClienteRequest;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarClienteInputPort;
import br.com.oficina.application.port.in.BuscarClientePorDocumentoInputPort;
import br.com.oficina.application.port.in.BuscarClientePorIdInputPort;
import br.com.oficina.application.port.in.CriarClienteInputPort;
import br.com.oficina.application.port.in.ExcluirClienteInputPort;
import br.com.oficina.application.port.in.ListarClientesInputPort;
import br.com.oficina.application.query.ClienteResult;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.infrastructure.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@Import({SecurityConfig.class, ClienteWebMapper.class, GlobalExceptionHandler.class})
@Epic("Atendimento ao Cliente")
@Feature("API REST Clientes")
class ClienteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private CriarClienteInputPort criarClienteInputPort;
    @MockitoBean private AtualizarClienteInputPort atualizarClienteInputPort;
    @MockitoBean private ExcluirClienteInputPort excluirClienteInputPort;
    @MockitoBean private BuscarClientePorIdInputPort buscarClientePorIdInputPort;
    @MockitoBean private BuscarClientePorDocumentoInputPort buscarClientePorDocumentoInputPort;
    @MockitoBean private ListarClientesInputPort listarClientesInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    private final ClienteResult resp = new ClienteResult(1L, "529.982.247-25", "CPF", "Joao", "11999", "j@e.com",
            "01001-000", "Praca da Se", "Se", "Sao Paulo", "SP");

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar cliente via API")
    void deveCriarCliente() throws Exception {
        when(criarClienteInputPort.execute(any())).thenReturn(resp);

        var req = new ClienteRequest("52998224725", "Joao", "11999", "j@e.com", "01001000");
        mockMvc.perform(post("/api/clientes").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Joao"))
            .andExpect(jsonPath("$.cep").value("01001-000"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar cliente por ID via API")
    void deveBuscarPorId() throws Exception {
        when(buscarClientePorIdInputPort.execute(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/clientes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar cliente por documento via API")
    void deveBuscarPorDocumento() throws Exception {
        when(buscarClientePorDocumentoInputPort.execute("52998224725")).thenReturn(resp);

        mockMvc.perform(get("/api/clientes/documento/52998224725"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.documento").value("529.982.247-25"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todos os clientes via API")
    void deveListarTodos() throws Exception {
        when(listarClientesInputPort.execute()).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar cliente via API")
    void deveAtualizar() throws Exception {
        when(atualizarClienteInputPort.execute(any())).thenReturn(resp);

        var req = new ClienteRequest("52998224725", "Joao", "11888", "novo@e.com", null);
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
    @Story("Rejeitar acesso sem autenticacao")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar criacao de cliente duplicado")
    void criarClienteComDocumentoDuplicado_retorna422() throws Exception {
        when(criarClienteInputPort.execute(any())).thenThrow(new NegocioException("Ja existe um cliente com esse documento"));

        var req = new ClienteRequest("52998224725", "Joao", "11999", "j@e.com", null);
        mockMvc.perform(post("/api/clientes").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Ja existe um cliente com esse documento"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao buscar por documento inexistente")
    void buscarPorDocumentoNaoEncontrado_retorna404() throws Exception {
        when(buscarClientePorDocumentoInputPort.execute("00000000000"))
            .thenThrow(new RecursoNaoEncontradoException("Cliente nao encontrado com documento: 00000000000"));

        mockMvc.perform(get("/api/clientes/documento/00000000000"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente nao encontrado com documento: 00000000000"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar cliente inexistente")
    void atualizarClienteNaoEncontrado_retorna404() throws Exception {
        when(atualizarClienteInputPort.execute(any()))
            .thenThrow(new RecursoNaoEncontradoException("Cliente nao encontrado: 99"));

        var req = new ClienteRequest("52998224725", "Joao", "11999", "j@e.com", null);
        mockMvc.perform(put("/api/clientes/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente nao encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir cliente inexistente")
    void excluirClienteNaoEncontrado_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Cliente nao encontrado: 99")).when(excluirClienteInputPort).execute(any());

        mockMvc.perform(delete("/api/clientes/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente nao encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void excluirClienteComVeiculos_retorna422() throws Exception {
        doThrow(new NegocioException("Nao e possivel excluir cliente com veiculos vinculados")).when(excluirClienteInputPort).execute(any());

        mockMvc.perform(delete("/api/clientes/1").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Nao e possivel excluir cliente com veiculos vinculados"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void excluirClienteComOrdensDeServico_retorna422() throws Exception {
        doThrow(new NegocioException("Nao e possivel excluir cliente com ordens de servico vinculadas")).when(excluirClienteInputPort).execute(any());

        mockMvc.perform(delete("/api/clientes/1").with(csrf()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Nao e possivel excluir cliente com ordens de servico vinculadas"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    void clienteNaoEncontrado_retorna404() throws Exception {
        when(buscarClientePorIdInputPort.execute(99L)).thenThrow(new RecursoNaoEncontradoException("Nao encontrado"));

        mockMvc.perform(get("/api/clientes/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Nao encontrado"));
    }
}
