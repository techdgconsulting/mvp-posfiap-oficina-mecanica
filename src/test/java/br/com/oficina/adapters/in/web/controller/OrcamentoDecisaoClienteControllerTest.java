package br.com.oficina.adapters.in.web.controller;

import br.com.oficina.application.port.in.AprovarOrcamentoPorTokenInputPort;
import br.com.oficina.application.port.in.RecusarOrcamentoPorTokenInputPort;
import br.com.oficina.application.port.out.TokenProviderPort;
import br.com.oficina.application.query.DecisaoOrcamentoClienteResult;
import br.com.oficina.infrastructure.security.SecurityConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrcamentoDecisaoClienteController.class)
@Import(SecurityConfig.class)
@Epic("Orcamento")
@Feature("Decisao externa de orcamento")
class OrcamentoDecisaoClienteControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AprovarOrcamentoPorTokenInputPort aprovarOrcamentoPorTokenInputPort;
    @MockitoBean private RecusarOrcamentoPorTokenInputPort recusarOrcamentoPorTokenInputPort;
    @MockitoBean private TokenProviderPort tokenProviderPort;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    @Story("Cliente aprova orcamento por token sem JWT")
    void deveAprovarOrcamentoPorTokenSemJwt() throws Exception {
        when(aprovarOrcamentoPorTokenInputPort.execute(any()))
                .thenReturn(new DecisaoOrcamentoClienteResult(
                        1L, "OS-2026-00001", "EM_EXECUCAO", "APROVADA",
                        "Orcamento aprovado com sucesso"));

        mockMvc.perform(post("/api/orcamentos/decisoes-cliente/token123/aprovar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decisao").value("APROVADA"))
            .andExpect(jsonPath("$.statusOrdemServico").value("EM_EXECUCAO"));

        verify(aprovarOrcamentoPorTokenInputPort).execute(any());
    }

    @Test
    @Story("Cliente recusa orcamento por token sem JWT")
    void deveRecusarOrcamentoPorTokenSemJwt() throws Exception {
        when(recusarOrcamentoPorTokenInputPort.executeRecusar(any()))
                .thenReturn(new DecisaoOrcamentoClienteResult(
                        1L, "OS-2026-00001", "CANCELADA", "RECUSADA",
                        "Orcamento recusado com sucesso"));

        mockMvc.perform(post("/api/orcamentos/decisoes-cliente/token123/recusar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decisao").value("RECUSADA"))
            .andExpect(jsonPath("$.statusOrdemServico").value("CANCELADA"));

        verify(recusarOrcamentoPorTokenInputPort).executeRecusar(any());
    }
}
