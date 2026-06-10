package br.com.oficina.interfaces.api;

import br.com.oficina.application.dto.ServicoResponse;
import br.com.oficina.application.service.ServicoService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

@WebMvcTest(ServicoController.class)
@Import(SecurityConfig.class)
@Epic("Catálogo de Serviços")
@Feature("API REST Serviços")
class ServicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private ServicoService servicoService;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;

    private final ServicoResponse resp = new ServicoResponse(1L, "Troca óleo", "Completa", new BigDecimal("150"), 30);

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar serviço via API")
    void deveCriar() throws Exception {
        when(servicoService.criar(any())).thenReturn(resp);
        mockMvc.perform(post("/api/servicos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Troca óleo", "valorUnitario", 150))))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar serviço por ID via API")
    void deveBuscar() throws Exception {
        when(servicoService.buscarPorId(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/servicos/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar serviços via API")
    void deveListar() throws Exception {
        when(servicoService.listarTodos()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/servicos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar serviço via API")
    void deveAtualizar() throws Exception {
        when(servicoService.atualizar(eq(1L), any())).thenReturn(resp);
        mockMvc.perform(put("/api/servicos/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Alinhamento", "valorUnitario", 80))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir serviço via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/servicos/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticação")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/servicos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 para serviço não encontrado")
    void servicoNaoEncontrado_retorna404() throws Exception {
        when(servicoService.buscarPorId(99L))
            .thenThrow(new RecursoNaoEncontradoException("Serviço não encontrado com id 99"));

        mockMvc.perform(get("/api/servicos/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Serviço não encontrado com id 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar serviço inexistente")
    void servicoNaoEncontradoAoAtualizar_retorna404() throws Exception {
        when(servicoService.atualizar(eq(99L), any()))
            .thenThrow(new RecursoNaoEncontradoException("Serviço não encontrado com id 99"));

        mockMvc.perform(put("/api/servicos/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("nome", "Troca do Carter", "valorUnitario", 100))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Serviço não encontrado com id 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir serviço inexistente")
    void servicoNaoEncontradoAoExcluir_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Serviço não encontrado com id 99")).when(servicoService).excluir(99L);

        mockMvc.perform(delete("/api/servicos/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Serviço não encontrado com id 99"));
    }
}

