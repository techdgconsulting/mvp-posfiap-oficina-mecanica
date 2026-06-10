package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.VeiculoResponse;
import br.com.oficina.application.service.VeiculoService;
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
import java.util.Map;

import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
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

@WebMvcTest(VeiculoController.class)
@Import(SecurityConfig.class)
@Epic("Atendimento ao Cliente")
@Feature("API REST Veículos")
class VeiculoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private VeiculoService veiculoService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;

    private final VeiculoResponse resp = new VeiculoResponse(1L, "ABC1D23", "Fiat", "Uno", 2020, 1L, "João");

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar veículo via API")
    void deveCriar() throws Exception {
        when(veiculoService.criar(any())).thenReturn(resp);

        mockMvc.perform(post("/api/veiculos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Fiat", "modelo", "Uno", "ano", 2020, "clienteId", 1))))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar veículo por ID via API")
    void deveBuscar() throws Exception {
        when(veiculoService.buscarPorId(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/veiculos/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar todos os veículos via API")
    void deveListar() throws Exception {
        when(veiculoService.listarTodos()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/veiculos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar veículos por cliente via API")
    void deveListarPorCliente() throws Exception {
        when(veiculoService.listarPorCliente(1L)).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/veiculos/cliente/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar veículo via API")
    void deveAtualizar() throws Exception {
        when(veiculoService.atualizar(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(put("/api/veiculos/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Honda", "modelo", "Civic", "ano", 2022, "clienteId", 1))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir veículo via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/veiculos/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticação")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/veiculos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 para veículo não encontrado")
    void veiculoNaoEncontrado_retorna404() throws Exception {
        when(veiculoService.buscarPorId(99L))
            .thenThrow(new RecursoNaoEncontradoException("Veículo não encontrado: 99"));

        mockMvc.perform(get("/api/veiculos/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Veículo não encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 quando cliente não encontrado ao criar veículo")
    void clienteNaoEncontradoAoCriarVeiculo_retorna404() throws Exception {
        when(veiculoService.criar(any()))
            .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado: 99"));

        mockMvc.perform(post("/api/veiculos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Fiat", "modelo", "Uno", "ano", 2020, "clienteId", 99))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Cliente não encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar placa duplicada")
    void placaDuplicada_retorna422() throws Exception {
        when(veiculoService.criar(any()))
            .thenThrow(new NegocioException("Já existe um veículo cadastrado com a placa: ABC1D23"));

        mockMvc.perform(post("/api/veiculos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Fiat", "modelo", "Uno", "ano", 2020, "clienteId", 1))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro").value("Já existe um veículo cadastrado com a placa: ABC1D23"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar veículo inexistente")
    void veiculoNaoEncontradoAoAtualizar_retorna404() throws Exception {
        when(veiculoService.atualizar(eq(99L), any()))
            .thenThrow(new RecursoNaoEncontradoException("Veículo não encontrado: 99"));

        mockMvc.perform(put("/api/veiculos/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "placa", "ABC1D23", "marca", "Honda", "modelo", "Civic", "ano", 2022, "clienteId", 1))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Veículo não encontrado: 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir veículo inexistente")
    void veiculoNaoEncontradoAoExcluir_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Veículo não encontrado: 99")).when(veiculoService).excluir(99L);

        mockMvc.perform(delete("/api/veiculos/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Veículo não encontrado: 99"));
    }
}

