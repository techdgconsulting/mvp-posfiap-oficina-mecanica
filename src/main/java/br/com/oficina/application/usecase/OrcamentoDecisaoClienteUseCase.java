package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AprovarOrcamentoCommand;
import br.com.oficina.application.command.DecidirOrcamentoPorTokenCommand;
import br.com.oficina.application.command.EnviarNotificacaoOrcamentoCommand;
import br.com.oficina.application.command.RejeitarOrcamentoCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.in.AprovarOrcamentoInputPort;
import br.com.oficina.application.port.in.AprovarOrcamentoPorTokenInputPort;
import br.com.oficina.application.port.in.EnviarNotificacaoOrcamentoInputPort;
import br.com.oficina.application.port.in.RecusarOrcamentoPorTokenInputPort;
import br.com.oficina.application.port.in.RejeitarOrcamentoInputPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.EmailNotificacaoPort;
import br.com.oficina.application.port.out.OrcamentoDecisaoClienteRepositoryPort;
import br.com.oficina.application.port.out.OrcamentoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.TokenSeguroPort;
import br.com.oficina.application.query.DecisaoOrcamentoClienteResult;
import br.com.oficina.application.query.NotificacaoOrcamentoResult;
import br.com.oficina.domain.model.OrcamentoDecisaoCliente;
import br.com.oficina.domain.valueobject.StatusDecisaoCliente;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrcamentoDecisaoClienteUseCase implements
        EnviarNotificacaoOrcamentoInputPort,
        AprovarOrcamentoPorTokenInputPort,
        RecusarOrcamentoPorTokenInputPort {

    private final OrdemDeServicoRepositoryPort osRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final OrcamentoRepositoryPort orcamentoRepository;
    private final OrcamentoDecisaoClienteRepositoryPort decisaoRepository;
    private final EmailNotificacaoPort emailNotificacaoPort;
    private final TokenSeguroPort tokenSeguroPort;
    private final AprovarOrcamentoInputPort aprovarOrcamentoInputPort;
    private final RejeitarOrcamentoInputPort rejeitarOrcamentoInputPort;
    private final String baseUrl;
    private final long expiracaoHoras;

    @Override
    public NotificacaoOrcamentoResult execute(EnviarNotificacaoOrcamentoCommand command) {
        var os = osRepository.buscarPorId(command.ordemServicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de servico nao encontrada: " + command.ordemServicoId()));
        var cliente = clienteRepository.buscarPorId(os.getClienteId()).orElse(os.getCliente());
        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new NegocioException("Cliente sem email cadastrado para notificacao de orcamento");
        }

        var orcamento = orcamentoRepository.buscarAtivoByOrdemDeServico(command.ordemServicoId())
                .orElseThrow(() -> new NegocioException("Nenhum orcamento ativo encontrado para esta OS"));

        decisaoRepository.listarPorOrcamentoEStatus(orcamento.getId(), StatusDecisaoCliente.PENDENTE)
                .forEach(decisao -> {
                    decisao.expirar();
                    decisaoRepository.salvar(decisao);
                });

        var token = tokenSeguroPort.gerarToken();
        var tokenHash = tokenSeguroPort.gerarHash(token);
        var dataExpiracao = LocalDateTime.now().plusHours(expiracaoHoras);
        var decisao = OrcamentoDecisaoCliente.criar(
                orcamento.getId(),
                command.ordemServicoId(),
                tokenHash,
                cliente.getEmail(),
                dataExpiracao);
        decisaoRepository.salvar(decisao);

        var linkAprovacao = montarLink(token, "aprovar");
        var linkRecusa = montarLink(token, "recusar");
        emailNotificacaoPort.enviar(new EmailNotificacaoPort.EmailNotificacao(
                cliente.getEmail(),
                "Orcamento da OS " + os.getNumero() + " disponivel para aprovacao",
                montarCorpoEmail(cliente.getNome(), os.getNumero(), orcamento.getValorTotal().toString(),
                        dataExpiracao, linkAprovacao, linkRecusa)));

        return new NotificacaoOrcamentoResult(
                orcamento.getId(),
                command.ordemServicoId(),
                os.getNumero(),
                cliente.getEmail(),
                dataExpiracao,
                linkAprovacao,
                linkRecusa);
    }

    @Override
    public DecisaoOrcamentoClienteResult execute(DecidirOrcamentoPorTokenCommand command) {
        return aprovar(command);
    }

    @Override
    public DecisaoOrcamentoClienteResult executeRecusar(DecidirOrcamentoPorTokenCommand command) {
        return recusar(command);
    }

    public DecisaoOrcamentoClienteResult aprovar(DecidirOrcamentoPorTokenCommand command) {
        var decisao = buscarDecisaoValida(command.token());
        var os = aprovarOrcamentoInputPort.execute(new AprovarOrcamentoCommand(decisao.getOrdemServicoId()));
        decisao.aprovar();
        decisaoRepository.salvar(decisao);
        return new DecisaoOrcamentoClienteResult(
                os.id(),
                os.numero(),
                os.status(),
                "APROVADA",
                "Orcamento aprovado com sucesso");
    }

    public DecisaoOrcamentoClienteResult recusar(DecidirOrcamentoPorTokenCommand command) {
        var decisao = buscarDecisaoValida(command.token());
        var os = rejeitarOrcamentoInputPort.execute(new RejeitarOrcamentoCommand(decisao.getOrdemServicoId()));
        decisao.recusar();
        decisaoRepository.salvar(decisao);
        return new DecisaoOrcamentoClienteResult(
                os.id(),
                os.numero(),
                os.status(),
                "RECUSADA",
                "Orcamento recusado com sucesso");
    }

    private OrcamentoDecisaoCliente buscarDecisaoValida(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token de decisao e obrigatorio");
        }
        var decisao = decisaoRepository.buscarPorTokenHash(tokenSeguroPort.gerarHash(token))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Token de decisao nao encontrado"));
        if (!decisao.estaPendente()) {
            throw new NegocioException("Token de decisao ja utilizado ou expirado");
        }
        if (decisao.estaExpirada()) {
            decisao.expirar();
            decisaoRepository.salvar(decisao);
            throw new NegocioException("Token de decisao expirado");
        }
        return decisao;
    }

    private String montarLink(String token, String acao) {
        return normalizarBaseUrl() + "/api/orcamentos/decisoes-cliente/" + token + "/" + acao;
    }

    private String normalizarBaseUrl() {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private static String montarCorpoEmail(
            String nomeCliente,
            String numeroOs,
            String valorTotal,
            LocalDateTime dataExpiracao,
            String linkAprovacao,
            String linkRecusa) {
        return """
                Ola, %s.

                O orcamento da OS %s esta disponivel para decisao.
                Valor total: R$ %s
                Validade do link: %s

                Aprovar orcamento: %s
                Recusar orcamento: %s

                Se voce nao reconhece esta OS, ignore esta mensagem.
                """.formatted(nomeCliente, numeroOs, valorTotal, dataExpiracao, linkAprovacao, linkRecusa);
    }
}
