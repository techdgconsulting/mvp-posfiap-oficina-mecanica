package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.adapters.in.web.mapper.ServicoWebMapper;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AtualizarServicoInputPort;
import br.com.oficina.application.port.in.BuscarServicoPorIdInputPort;
import br.com.oficina.application.port.in.CriarServicoInputPort;
import br.com.oficina.application.port.in.ExcluirServicoInputPort;
import br.com.oficina.application.port.in.ListarServicosInputPort;
import br.com.oficina.application.query.ServicoResult;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.infrastructure.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.math.BigDecimal;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServicoController.class)
@Import({SecurityConfig.class, ServicoWebMapper.class})
@Epic("Catalogo de Servicos")
@Feature("API REST Servicos")
class ServicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private CriarServicoInputPort criarServicoInputPort;
    @MockitoBean private BuscarServicoPorIdInputPort buscarServicoPorIdInputPort;
    @MockitoBean private ListarServicosInputPort listarServicosInputPort;
    @MockitoBean private AtualizarServicoInputPort atualizarServicoInputPort;
    @MockitoBean private ExcluirServicoInputPort excluirServicoInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    private final ServicoResult resp = new ServicoResult(1L, "Troca oleo", "Completa", new BigDecimal("150"), 30);

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Criar servico via API")
    void deveCriar() throws Exception {
        when(criarServicoInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(post("/api/servicos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Troca oleo", "valorUnitario", 150))))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Buscar servico por ID via API")
    void deveBuscar() throws Exception {
        when(buscarServicoPorIdInputPort.execute(1L)).thenReturn(resp);
        mockMvc.perform(get("/api/servicos/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listar servicos via API")
    void deveListar() throws Exception {
        when(listarServicosInputPort.execute()).thenReturn(List.of(resp));
        mockMvc.perform(get("/api/servicos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Listagem de servicos nao reflete payload suspeito")
    void deveSanitizarPayloadSuspeitoNaListagem() throws Exception {
        var suspeito = new ServicoResult(
            2L,
            "John Doe <!--SELECT-->",
            "${__import__('subprocess')}",
            new BigDecimal("150"),
            30
        );
        when(listarServicosInputPort.execute()).thenReturn(List.of(suspeito));

        mockMvc.perform(get("/api/servicos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nome").value("[conteudo removido]"))
            .andExpect(jsonPath("$[0].descricao").value("[conteudo removido]"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Rejeitar servico com payload suspeito")
    void deveRejeitarPayloadSuspeitoAoCriar() throws Exception {
        mockMvc.perform(post("/api/servicos").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "<xsl:value-of select=\"system-property('xsl:vendor')\"/>",
                    "descricao", "Servico",
                    "valorUnitario", 150))))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Atualizar servico via API")
    void deveAtualizar() throws Exception {
        when(atualizarServicoInputPort.execute(any())).thenReturn(resp);
        mockMvc.perform(put("/api/servicos/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                    "nome", "Alinhamento", "valorUnitario", 80))))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Excluir servico via API")
    void deveExcluir() throws Exception {
        mockMvc.perform(delete("/api/servicos/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @Story("Rejeitar acesso sem autenticacao")
    void semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(get("/api/servicos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 para servico nao encontrado")
    void servicoNaoEncontrado_retorna404() throws Exception {
        when(buscarServicoPorIdInputPort.execute(99L))
            .thenThrow(new RecursoNaoEncontradoException("Servico nao encontrado com id 99"));

        mockMvc.perform(get("/api/servicos/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Servico nao encontrado com id 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao atualizar servico inexistente")
    void servicoNaoEncontradoAoAtualizar_retorna404() throws Exception {
        when(atualizarServicoInputPort.execute(any()))
            .thenThrow(new RecursoNaoEncontradoException("Servico nao encontrado com id 99"));

        mockMvc.perform(put("/api/servicos/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("nome", "Troca do Carter", "valorUnitario", 100))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Servico nao encontrado com id 99"));
    }

    @Test
    @WithMockUser(roles = "GESTOR")
    @Story("Retornar 404 ao excluir servico inexistente")
    void servicoNaoEncontradoAoExcluir_retorna404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Servico nao encontrado com id 99"))
                .when(excluirServicoInputPort).execute(eq(new br.com.oficina.application.command.ExcluirServicoCommand(99L)));

        mockMvc.perform(delete("/api/servicos/99").with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").value("Servico nao encontrado com id 99"));
    }
}
