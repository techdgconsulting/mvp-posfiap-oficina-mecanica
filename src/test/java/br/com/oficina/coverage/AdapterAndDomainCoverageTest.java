package br.com.oficina.coverage;

import br.com.oficina.adapters.out.notification.LogEmailNotificacaoAdapter;
import br.com.oficina.adapters.out.persistence.OrcamentoDecisaoClientePersistenceAdapter;
import br.com.oficina.adapters.out.persistence.mapper.OrcamentoDecisaoClientePersistenceMapper;
import br.com.oficina.adapters.out.persistence.repository.SpringDataOrcamentoDecisaoClienteRepository;
import br.com.oficina.adapters.out.security.SecureTokenAdapter;
import br.com.oficina.adapters.out.security.PasswordHasherAdapter;
import br.com.oficina.adapters.out.viacep.ViaCepEnderecoAdapter;
import br.com.oficina.adapters.out.viacep.ViaCepEnderecoResponse;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.OrcamentoDecisaoCliente;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.model.Usuario;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.PerfilUsuario;
import br.com.oficina.domain.valueobject.StatusDecisaoCliente;
import br.com.oficina.domain.valueobject.StatusExecucao;
import br.com.oficina.domain.valueobject.StatusPagamento;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class AdapterAndDomainCoverageTest {

    @Test
    void viaCepRetornaEnderecoETrataEntradasInvalidas() {
        var restTemplate = mock(RestTemplate.class);
        var adapter = new ViaCepEnderecoAdapter(restTemplate);
        var response = new ViaCepEnderecoResponse("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP", false);

        when(restTemplate.getForObject("https://viacep.com.br/ws/{cep}/json/", ViaCepEnderecoResponse.class, "01001000"))
                .thenReturn(response);
        when(restTemplate.getForObject("https://viacep.com.br/ws/{cep}/json/", ViaCepEnderecoResponse.class, "99999999"))
                .thenReturn(new ViaCepEnderecoResponse(null, null, null, null, null, true));
        when(restTemplate.getForObject("https://viacep.com.br/ws/{cep}/json/", ViaCepEnderecoResponse.class, "88888888"))
                .thenThrow(new RestClientException("fora"));

        assertTrue(adapter.buscarPorCep("01001-000").isPresent());
        assertTrue(adapter.buscarPorCep("123").isEmpty());
        assertTrue(adapter.buscarPorCep("99999-999").isEmpty());
        assertTrue(adapter.buscarPorCep("88888-888").isEmpty());
    }

    @Test
    void passwordHasherDelegaParaEncoder() {
        var encoder = mock(PasswordEncoder.class);
        when(encoder.encode("senha")).thenReturn("hash");
        when(encoder.matches("senha", "hash")).thenReturn(true);

        var adapter = new PasswordHasherAdapter(encoder);

        assertEquals("hash", adapter.hash("senha"));
        assertTrue(adapter.matches("senha", "hash"));
    }

    @Test
    void secureTokenGeraTokenEHashDeterministico() {
        var adapter = new SecureTokenAdapter();

        var token = adapter.gerarToken();
        var hash1 = adapter.gerarHash(token);
        var hash2 = adapter.gerarHash(token);

        assertEquals(64, token.length());
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    void emailLogAdapterAceitaMensagem() {
        var adapter = new LogEmailNotificacaoAdapter();

        adapter.enviar(new EmailNotificacaoPort.EmailNotificacao(
                "cliente@email.com",
                "Orcamento disponivel",
                "Aprovar: http://localhost/aprovar"));
    }

    @Test
    void decisaoClienteValidaTransicoesEExpiracao() {
        assertThrows(IllegalArgumentException.class,
                () -> OrcamentoDecisaoCliente.criar(null, 1L, "hash", "email@x.com", LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class,
                () -> OrcamentoDecisaoCliente.criar(1L, 1L, "", "email@x.com", LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class,
                () -> OrcamentoDecisaoCliente.criar(1L, 1L, "hash", "", LocalDateTime.now()));

        var aprovada = OrcamentoDecisaoCliente.criar(
                1L, 1L, "hash", "email@x.com", LocalDateTime.now().plusHours(1));
        aprovada.aprovar();

        assertEquals(StatusDecisaoCliente.APROVADA, aprovada.getStatus());
        assertThrows(IllegalStateException.class, aprovada::recusar);

        var expirada = OrcamentoDecisaoCliente.criar(
                1L, 1L, "hash2", "email@x.com", LocalDateTime.now().minusHours(1));
        assertTrue(expirada.estaExpirada());
        assertThrows(IllegalStateException.class, expirada::aprovar);
        assertEquals(StatusDecisaoCliente.EXPIRADA, expirada.getStatus());
    }

    @Test
    void orcamentoDecisaoPersistenceAdapterMapeiaOperacoes() {
        var repository = mock(SpringDataOrcamentoDecisaoClienteRepository.class);
        var mapper = new OrcamentoDecisaoClientePersistenceMapper();
        var adapter = new OrcamentoDecisaoClientePersistenceAdapter(repository, mapper);
        var decisao = OrcamentoDecisaoCliente.builder()
                .id(1L)
                .orcamentoId(10L)
                .ordemServicoId(20L)
                .tokenHash("hash")
                .status(StatusDecisaoCliente.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusHours(1))
                .emailDestino("cliente@email.com")
                .build();
        var entity = mapper.toEntity(decisao);

        when(repository.save(any())).thenReturn(entity);
        when(repository.findByTokenHash("hash")).thenReturn(Optional.of(entity));
        when(repository.findByOrcamentoIdAndStatus(10L, StatusDecisaoCliente.PENDENTE)).thenReturn(List.of(entity));

        assertEquals(1L, adapter.salvar(decisao).getId());
        assertTrue(adapter.buscarPorTokenHash("hash").isPresent());
        assertEquals(1, adapter.listarPorOrcamentoEStatus(10L, StatusDecisaoCliente.PENDENTE).size());
        verify(repository).save(any());
    }

    @Test
    void usuarioValidaCamposObrigatoriosERole() {
        var usuario = Usuario.criar("atendente", "senha", "ATENDENTE");

        assertEquals("ATENDENTE", usuario.getRole());
        assertEquals(PerfilUsuario.ATENDENTE, usuario.getPerfil());
        assertThrows(IllegalArgumentException.class, () -> Usuario.criar("", "senha", "ATENDENTE"));
        assertThrows(IllegalArgumentException.class, () -> Usuario.criar("user", "", "ATENDENTE"));
        assertThrows(IllegalArgumentException.class, () -> new Usuario(1L, "user", "senha", null));
        assertEquals(PerfilUsuario.MECANICO, PerfilUsuario.from("mecanico"));
        assertFalse(PerfilUsuario.isValido(null));
        assertFalse(PerfilUsuario.isValido("INVALIDO"));
    }

    @Test
    void pagamentoValidaCriacaoETransicoesInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> Pagamento.criar(1L, BigDecimal.ZERO, MetodoPagamento.PIX));
        assertThrows(IllegalArgumentException.class, () -> Pagamento.criar(1L, BigDecimal.TEN, null));

        var pagamento = Pagamento.builder()
                .id(1L)
                .ordemDeServicoId(1L)
                .status(StatusPagamento.PENDENTE)
                .metodo(MetodoPagamento.PIX)
                .valor(BigDecimal.TEN)
                .build();
        pagamento.recusar();

        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatus());
        assertThrows(IllegalStateException.class, pagamento::aprovar);
    }

    @Test
    void execucaoValidaEstadosObrigatorios() {
        var execucao = Execucao.criar(1L);

        assertThrows(IllegalArgumentException.class, () -> execucao.iniciarDiagnostico(""));
        execucao.iniciarDiagnostico("Mecanico");
        assertThrows(IllegalStateException.class, () -> execucao.iniciarDiagnostico("Outro"));
        execucao.registrarDiagnostico("Falha identificada");
        execucao.iniciarServico();
        execucao.finalizarServico();

        assertEquals(StatusExecucao.SERVICO_FINALIZADO, execucao.getStatus());
        assertThrows(IllegalStateException.class, execucao::finalizarServico);
        assertThrows(IllegalArgumentException.class, () -> execucao.atualizarMecanicoResponsavel(" "));
    }
}
