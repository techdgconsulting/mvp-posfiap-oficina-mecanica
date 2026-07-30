package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.VeiculoWebMapper;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarVeiculoInputPort;
import br.com.oficina.application.port.in.BuscarVeiculoPorIdInputPort;
import br.com.oficina.application.port.in.CriarVeiculoInputPort;
import br.com.oficina.application.port.in.ExcluirVeiculoInputPort;
import br.com.oficina.application.port.in.ListarVeiculosInputPort;
import br.com.oficina.application.port.in.ListarVeiculosPorClienteInputPort;
import br.com.oficina.application.query.VeiculoResult;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.infrastructure.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.List;
import java.util.Map;
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

@WebMvcTest(VeiculoController.class)
@Import({SecurityConfig.class, VeiculoWebMapper.class, GlobalExceptionHandler.class})
@Epic("Atendimento ao Cliente")
@Feature("API REST Veiculos")
class VeiculoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private CriarVeiculoInputPort criarVeiculoInputPort;
    @MockitoBean private AtualizarVeiculoInputPort atualizarVeiculoInputPort;
    @MockitoBean private ExcluirVeiculoInputPort excluirVeiculoInputPort;
    @MockitoBean private BuscarVeiculoPorIdInputPort buscarVeiculoPorIdInputPort;
    @MockitoBean private ListarVeiculosInputPort listarVeiculosInputPort;
    @MockitoBean private ListarVeiculosPorClienteInputPort listarVeiculosPorClienteInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    private final VeiculoResult resp = new VeiculoResult(1L, "ABC1D23", "Fiat", "Uno", 2020, 1L, "Joao");

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar veiculo via API")
    void deveCriar() throws Exception {
        when(criarVeiculoInputPort.execute(any())).thenReturn(resp);

        mockMvc.perform(post("/api/veiculos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Fiat", "modelo", "Uno", "ano", 2020, "clienteId", 1))))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar veiculo por ID via API")
    void deveBuscar() throws Exception {
        when(buscarVeiculoPorIdInputPort.execute(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/veiculos/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todos os veiculos via API")
    void deveListar() throws Exception {
        when(listarVeiculosInputPort.execute()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/veiculos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar veiculos por cliente via API")
    void deveListarPorCliente() throws Exception {
        when(listarVeiculosPorClienteInputPort.execute(1L)).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/veiculos/cliente/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar veiculo via API")
    void deveAtualizar() throws Exception {
        when(atualizarVeiculoInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(put("/api/veiculos/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Honda", "modelo", "Civic", "ano", 2022, "clienteId", 1))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir veiculo via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/veiculos/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticacao")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/veiculos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 para veiculo nao encontrado")
    void veiculoNaoEncontrado_retorna404() throws Exception {
        when(buscarVeiculoPorIdInputPort.execute(99L))
            .thenThrow(new RecursoNaoEncontradoException("Veiculo nao encontrado: 99"));

        mockMvc.perform(get("/api/veiculos/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Veiculo nao encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 quando cliente nao encontrado ao criar veiculo")
    void clienteNaoEncontradoAoCriarVeiculo_retorna404() throws Exception {
        when(criarVeiculoInputPort.execute(any()))
            .thenThrow(new RecursoNaoEncontradoException("Cliente nao encontrado: 99"));

        mockMvc.perform(post("/api/veiculos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Fiat", "modelo", "Uno", "ano", 2020, "clienteId", 99))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente nao encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar placa duplicada")
    void placaDuplicada_retorna422() throws Exception {
        when(criarVeiculoInputPort.execute(any()))
            .thenThrow(new NegocioException("Ja existe um veiculo cadastrado com a placa: ABC1D23"));

        mockMvc.perform(post("/api/veiculos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Fiat", "modelo", "Uno", "ano", 2020, "clienteId", 1))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Ja existe um veiculo cadastrado com a placa: ABC1D23"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar veiculo inexistente")
    void veiculoNaoEncontradoAoAtualizar_retorna404() throws Exception {
        when(atualizarVeiculoInputPort.execute(any()))
            .thenThrow(new RecursoNaoEncontradoException("Veiculo nao encontrado: 99"));

        mockMvc.perform(put("/api/veiculos/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Honda", "modelo", "Civic", "ano", 2022, "clienteId", 1))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Veiculo nao encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir veiculo inexistente")
    void veiculoNaoEncontradoAoExcluir_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Veiculo nao encontrado: 99")).when(excluirVeiculoInputPort).execute(any());

        mockMvc.perform(delete("/api/veiculos/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Veiculo nao encontrado: 99"));
    }
}
