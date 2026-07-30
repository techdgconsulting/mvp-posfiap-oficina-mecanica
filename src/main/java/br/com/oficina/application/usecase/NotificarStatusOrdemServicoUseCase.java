package br.com.oficina.application.usecase;

import br.com.oficina.application.command.NotificarStatusOrdemServicoCommand;
import br.com.oficina.application.port.in.NotificarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import br.com.oficina.application.query.NotificacaoStatusOrdemServicoResult;

public class NotificarStatusOrdemServicoUseCase implements NotificarStatusOrdemServicoInputPort {

    private final EmailNotificacaoPort emailNotificacaoPort;
    private final String baseUrl;

    public NotificarStatusOrdemServicoUseCase(EmailNotificacaoPort emailNotificacaoPort, String baseUrl) {
        this.emailNotificacaoPort = emailNotificacaoPort;
        this.baseUrl = baseUrl;
    }

    @Override
    public NotificacaoStatusOrdemServicoResult execute(NotificarStatusOrdemServicoCommand command) {
        if (command.clienteEmail() == null || command.clienteEmail().isBlank()) {
            return new NotificacaoStatusOrdemServicoResult(
                    command.ordemServicoId(),
                    command.numeroOS(),
                    command.status(),
                    command.clienteEmail(),
                    false,
                    "Cliente sem e-mail cadastrado");
        }

        var assunto = "Atualizacao da OS " + command.numeroOS() + ": " + command.status();
        var corpo = montarCorpo(command);

        emailNotificacaoPort.enviar(new EmailNotificacaoPort.EmailNotificacao(
                command.clienteEmail(),
                assunto,
                corpo));

        return new NotificacaoStatusOrdemServicoResult(
                command.ordemServicoId(),
                command.numeroOS(),
                command.status(),
                command.clienteEmail(),
                true,
                "Notificacao de status enviada");
    }

    private String montarCorpo(NotificarStatusOrdemServicoCommand command) {
        var nome = command.clienteNome() == null || command.clienteNome().isBlank()
                ? "cliente"
                : command.clienteNome();
        var linkStatus = normalizarBaseUrl() + "/api/ordens-servico/numero/" + command.numeroOS() + "/status";

        return """
                Ola, %s.

                A Ordem de Servico %s teve uma atualizacao de status.

                Status atual: %s
                %s

                Voce pode consultar o acompanhamento pelo link:
                %s

                Este e-mail e informativo. Em caso de duvidas, entre em contato com a oficina.

                Att.: Equipe Oficina DGCar

                """.formatted(
                nome,
                command.numeroOS(),
                command.status(),
                mensagemAmigavel(command.status()),
                linkStatus);
    }

    private String mensagemAmigavel(String status) {
        return switch (status) {
            case "RECEBIDA" -> "Sua ordem de servico foi recebida.";
            case "EM_DIAGNOSTICO" -> "Seu veiculo esta em diagnostico.";
            case "AGUARDANDO_APROVACAO" -> "O orcamento esta aguardando aprovacao.";
            case "EM_EXECUCAO" -> "O servico foi aprovado e esta em execucao.";
            case "FINALIZADA" -> "O servico foi finalizado.";
            case "AGUARDANDO_RETIRADA" -> "Seu veiculo esta aguardando retirada.";
            case "ENTREGUE" -> "Seu veiculo foi entregue.";
            case "CANCELADA" -> "A ordem de servico foi cancelada.";
            default -> "Consulte a oficina para mais detalhes sobre o andamento.";
        };
    }

    private String normalizarBaseUrl() {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
