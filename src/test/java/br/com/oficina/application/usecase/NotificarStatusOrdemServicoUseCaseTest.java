package br.com.oficina.application.usecase;

import br.com.oficina.application.command.NotificarStatusOrdemServicoCommand;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificarStatusOrdemServicoUseCaseTest {

    @Mock private EmailNotificacaoPort emailNotificacaoPort;

    private NotificarStatusOrdemServicoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new NotificarStatusOrdemServicoUseCase(emailNotificacaoPort, "http://localhost:8080/");
    }

    @Test
    void deveEnviarEmailInformativoComStatusAtualELinkDeConsulta() {
        var command = new NotificarStatusOrdemServicoCommand(
                1L,
                "OS-2026-00001",
                "EM_DIAGNOSTICO",
                "Maria",
                "maria@email.com");

        var result = useCase.execute(command);

        assertTrue(result.enviado());

        var captor = ArgumentCaptor.forClass(EmailNotificacaoPort.EmailNotificacao.class);
        verify(emailNotificacaoPort).enviar(captor.capture());

        var email = captor.getValue();
        assertTrue(email.assunto().contains("OS-2026-00001"));
        assertTrue(email.assunto().contains("EM_DIAGNOSTICO"));
        assertTrue(email.corpo().contains("Seu veiculo esta em diagnostico."));
        assertTrue(email.corpo().contains("http://localhost:8080/api/ordens-servico/numero/OS-2026-00001/status"));
    }

    @Test
    void naoDeveEnviarEmailQuandoClienteNaoPossuiEmailCadastrado() {
        var command = new NotificarStatusOrdemServicoCommand(
                1L,
                "OS-2026-00001",
                "RECEBIDA",
                "Maria",
                null);

        var result = useCase.execute(command);

        assertFalse(result.enviado());
        verify(emailNotificacaoPort, never()).enviar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void naoDeveEnviarEmailQuandoClientePossuiEmailEmBranco() {
        var command = new NotificarStatusOrdemServicoCommand(
                1L,
                "OS-2026-00001",
                "RECEBIDA",
                "Maria",
                " ");

        var result = useCase.execute(command);

        assertFalse(result.enviado());
        verify(emailNotificacaoPort, never()).enviar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveUsarSaudacaoGenericaQuandoNomeNaoInformado() {
        var command = new NotificarStatusOrdemServicoCommand(
                1L,
                "OS-2026-00001",
                "ENTREGUE",
                "",
                "cliente@email.com");

        useCase.execute(command);

        var captor = ArgumentCaptor.forClass(EmailNotificacaoPort.EmailNotificacao.class);
        verify(emailNotificacaoPort).enviar(captor.capture());

        assertTrue(captor.getValue().corpo().contains("Ola, cliente."));
    }

    @ParameterizedTest
    @CsvSource({
            "RECEBIDA,Sua ordem de servico foi recebida.",
            "AGUARDANDO_APROVACAO,O orcamento esta aguardando aprovacao.",
            "EM_EXECUCAO,O servico foi aprovado e esta em execucao.",
            "FINALIZADA,O servico foi finalizado.",
            "AGUARDANDO_RETIRADA,Seu veiculo esta aguardando retirada.",
            "ENTREGUE,Seu veiculo foi entregue.",
            "CANCELADA,A ordem de servico foi cancelada.",
            "STATUS_DESCONHECIDO,Consulte a oficina para mais detalhes sobre o andamento."
    })
    void deveMapearMensagemAmigavelPorStatus(String status, String mensagemEsperada) {
        var command = new NotificarStatusOrdemServicoCommand(
                1L,
                "OS-2026-00001",
                status,
                "Maria",
                "maria@email.com");

        useCase.execute(command);

        var captor = ArgumentCaptor.forClass(EmailNotificacaoPort.EmailNotificacao.class);
        verify(emailNotificacaoPort).enviar(captor.capture());

        assertTrue(captor.getValue().corpo().contains(mensagemEsperada));
    }
}
