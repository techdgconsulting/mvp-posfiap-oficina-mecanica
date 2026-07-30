package br.com.oficina.application.usecase;

import br.com.oficina.application.command.AprovarPagamentoCommand;
import br.com.oficina.application.command.ProcessarPagamentoCommand;
import br.com.oficina.application.command.RecusarPagamentoCommand;
import br.com.oficina.application.exception.RecursoNaoEncontradoException;
import br.com.oficina.application.port.out.PagamentoGatewayPort;
import br.com.oficina.application.port.out.PagamentoRepositoryPort;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.StatusPagamento;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoUseCaseTest {

    @Mock
    private PagamentoRepositoryPort pagamentoRepository;

    @Mock
    private PagamentoGatewayPort pagamentoGateway;

    private AprovarPagamentoUseCase aprovarPagamentoUseCase;
    private RecusarPagamentoUseCase recusarPagamentoUseCase;
    private ProcessarPagamentoUseCase processarPagamentoUseCase;

    @BeforeEach
    void setUp() {
        aprovarPagamentoUseCase = new AprovarPagamentoUseCase(pagamentoRepository);
        recusarPagamentoUseCase = new RecusarPagamentoUseCase(pagamentoRepository);
        processarPagamentoUseCase = new ProcessarPagamentoUseCase(pagamentoRepository, pagamentoGateway);
    }

    @Test
    void aprovarPagamentoPendente() {
        var pagamento = pagamentoPendente();
        when(pagamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = aprovarPagamentoUseCase.execute(new AprovarPagamentoCommand(1L, "tx-123", "Aprovado"));

        assertEquals("APROVADO", result.status());
        assertEquals("tx-123", result.transactionId());
        assertEquals("Aprovado", result.gatewayMensagem());
        assertNotNull(result.dataPagamento());
        verify(pagamentoRepository).salvar(pagamento);
    }

    @Test
    void recusarPagamentoPendente() {
        var pagamento = pagamentoPendente();
        when(pagamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = recusarPagamentoUseCase.execute(new RecusarPagamentoCommand(1L, "tx-999", "Saldo insuficiente"));

        assertEquals("RECUSADO", result.status());
        assertEquals("tx-999", result.transactionId());
        assertEquals("Saldo insuficiente", result.gatewayMensagem());
        verify(pagamentoRepository).salvar(pagamento);
    }

    @Test
    void processarPagamentoAprovadoNoGateway() {
        var pagamento = pagamentoPendente();
        when(pagamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoGateway.processar(any())).thenReturn(PagamentoGatewayPort.GatewayResponse.aprovado("gw-1", "OK"));
        when(pagamentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = processarPagamentoUseCase.execute(new ProcessarPagamentoCommand(1L));

        assertEquals("APROVADO", result.status());
        assertEquals("gw-1", result.transactionId());

        var requestCaptor = ArgumentCaptor.forClass(PagamentoGatewayPort.GatewayRequest.class);
        verify(pagamentoGateway).processar(requestCaptor.capture());
        assertEquals(10L, requestCaptor.getValue().ordemServicoId());
        assertEquals(new BigDecimal("120.00"), requestCaptor.getValue().valor());
        assertEquals(MetodoPagamento.CARTAO_CREDITO, requestCaptor.getValue().metodo());
    }

    @Test
    void processarPagamentoRecusadoNoGateway() {
        var pagamento = pagamentoPendente();
        when(pagamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoGateway.processar(any())).thenReturn(PagamentoGatewayPort.GatewayResponse.recusado("gw-2", "Recusado"));
        when(pagamentoRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = processarPagamentoUseCase.execute(new ProcessarPagamentoCommand(1L));

        assertEquals("RECUSADO", result.status());
        assertEquals("gw-2", result.transactionId());
        assertEquals("Recusado", result.gatewayMensagem());
    }

    @Test
    void rejeitaPagamentoInexistente() {
        when(pagamentoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> aprovarPagamentoUseCase.execute(new AprovarPagamentoCommand(99L, "tx", "OK")));
    }

    @Test
    void dominioImpedeReprocessarPagamento() {
        var pagamento = Pagamento.builder()
                .id(1L)
                .ordemDeServicoId(10L)
                .status(StatusPagamento.APROVADO)
                .metodo(MetodoPagamento.PIX)
                .valor(new BigDecimal("120.00"))
                .build();

        assertThrows(IllegalStateException.class, () -> pagamento.recusar("tx", "Recusado"));
        assertThrows(IllegalStateException.class, () -> pagamento.aprovar("tx", "OK"));
    }

    private Pagamento pagamentoPendente() {
        return Pagamento.builder()
                .id(1L)
                .ordemDeServicoId(10L)
                .status(StatusPagamento.PENDENTE)
                .metodo(MetodoPagamento.CARTAO_CREDITO)
                .valor(new BigDecimal("120.00"))
                .build();
    }
}
