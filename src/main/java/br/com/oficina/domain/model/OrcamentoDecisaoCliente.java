package br.com.oficina.domain.model;

import br.com.oficina.domain.valueobject.StatusDecisaoCliente;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoDecisaoCliente {

    private Long id;
    private Long orcamentoId;
    private Long ordemServicoId;
    private String tokenHash;
    @Builder.Default
    private StatusDecisaoCliente status = StatusDecisaoCliente.PENDENTE;
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataExpiracao;
    private LocalDateTime dataDecisao;
    private String emailDestino;

    public static OrcamentoDecisaoCliente criar(
            Long orcamentoId,
            Long ordemServicoId,
            String tokenHash,
            String emailDestino,
            LocalDateTime dataExpiracao) {
        if (orcamentoId == null || ordemServicoId == null) {
            throw new IllegalArgumentException("Orcamento e OS sao obrigatorios para decisao do cliente");
        }
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new IllegalArgumentException("Token hash e obrigatorio");
        }
        if (emailDestino == null || emailDestino.isBlank()) {
            throw new IllegalArgumentException("Email do cliente e obrigatorio para notificacao");
        }
        return OrcamentoDecisaoCliente.builder()
                .orcamentoId(orcamentoId)
                .ordemServicoId(ordemServicoId)
                .tokenHash(tokenHash)
                .emailDestino(emailDestino)
                .dataExpiracao(dataExpiracao)
                .build();
    }

    public void aprovar() {
        validarPendente();
        this.status = StatusDecisaoCliente.APROVADA;
        this.dataDecisao = LocalDateTime.now();
    }

    public void recusar() {
        validarPendente();
        this.status = StatusDecisaoCliente.RECUSADA;
        this.dataDecisao = LocalDateTime.now();
    }

    public void expirar() {
        if (this.status == StatusDecisaoCliente.PENDENTE) {
            this.status = StatusDecisaoCliente.EXPIRADA;
            this.dataDecisao = LocalDateTime.now();
        }
    }

    public boolean estaExpirada() {
        return dataExpiracao != null && LocalDateTime.now().isAfter(dataExpiracao);
    }

    public boolean estaPendente() {
        return status == StatusDecisaoCliente.PENDENTE;
    }

    private void validarPendente() {
        if (status != StatusDecisaoCliente.PENDENTE) {
            throw new IllegalStateException("Decisao do cliente ja processada: " + status);
        }
        if (estaExpirada()) {
            expirar();
            throw new IllegalStateException("Token de decisao expirado");
        }
    }
}
