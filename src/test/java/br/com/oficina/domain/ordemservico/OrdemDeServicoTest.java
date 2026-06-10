package br.com.oficina.domain.ordemservico;

import br.com.oficina.domain.atendimento.cliente.Cliente;
import br.com.oficina.domain.atendimento.cliente.vo.CpfCnpj;
import br.com.oficina.domain.atendimento.veiculo.Veiculo;
import br.com.oficina.domain.atendimento.veiculo.vo.Placa;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Ordem de Serviço")
@Feature("Máquina de Estados")
class OrdemDeServicoTest {

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("João Silva")
                .build();

        veiculo = Veiculo.builder()
                .id(1L)
                .placa(new Placa("ABC1D23"))
                .marca("Fiat")
                .modelo("Uno")
                .ano(2020)
                .cliente(cliente)
                .build();
    }

    @Test
    @Story("Criar OS com status inicial RECEBIDA")
    void deveCriarOSComStatusRecebida() {
        var os = OrdemDeServico.criar(cliente, veiculo);

        assertEquals(StatusOS.RECEBIDA, os.getStatus());
        assertNotNull(os.getDataCriacao());
        assertNull(os.getDataFinalizacao());
    }

    @Test
    @Story("Rejeitar criação de OS sem cliente")
    void naoDeveCriarSemCliente() {
        assertThrows(IllegalArgumentException.class, () -> OrdemDeServico.criar(null, veiculo));
    }

    @Test
    @Story("Rejeitar criação de OS sem veículo")
    void naoDeveCriarSemVeiculo() {
        assertThrows(IllegalArgumentException.class, () -> OrdemDeServico.criar(cliente, null));
    }

    @Test
    @Story("Adicionar itens e calcular valor total")
    void deveAdicionarItemECalcularTotal() {
        var os = OrdemDeServico.criar(cliente, veiculo);

        var item1 = ItemOS.builder()
                .tipo(TipoItem.SERVICO)
                .descricao("Troca de óleo")
                .quantidade(1)
                .valorUnitario(new BigDecimal("150.00"))
                .build();

        var item2 = ItemOS.builder()
                .tipo(TipoItem.PECA)
                .descricao("Filtro de óleo")
                .quantidade(2)
                .valorUnitario(new BigDecimal("35.00"))
                .build();

        os.adicionarItem(item1);
        os.adicionarItem(item2);

        assertEquals(2, os.getItens().size());
        assertEquals(new BigDecimal("220.00"), os.calcularValorTotal());
    }

    @Test
    @Story("Percorrer fluxo completo RECEBIDA → ENTREGUE")
    void devePercorrerFluxoCompleto() {
        var os = OrdemDeServico.criar(cliente, veiculo);

        // RECEBIDA -> EM_DIAGNOSTICO
        os.avancarParaDiagnostico();
        assertEquals(StatusOS.EM_DIAGNOSTICO, os.getStatus());

        // EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO
        os.aguardarAprovacao();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, os.getStatus());

        // AGUARDANDO_APROVACAO -> EM_EXECUCAO
        os.aprovarEIniciarExecucao();
        assertEquals(StatusOS.EM_EXECUCAO, os.getStatus());

        // EM_EXECUCAO -> FINALIZADA
        os.finalizar();
        assertEquals(StatusOS.FINALIZADA, os.getStatus());
        assertNotNull(os.getDataFinalizacao());

        // FINALIZADA -> AGUARDANDO_RETIRADA (pagamento aprovado, cliente ainda não veio buscar)
        os.aguardarRetirada();
        assertEquals(StatusOS.AGUARDANDO_RETIRADA, os.getStatus());

        // AGUARDANDO_RETIRADA -> ENTREGUE (cliente veio buscar o veículo)
        os.entregar();
        assertEquals(StatusOS.ENTREGUE, os.getStatus());
    }

    @Test
    @Story("Retornar a AGUARDANDO_APROVACAO por novo problema")
    void devePermitirVoltarParaAprovacaoQuandoNovoProblema() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();

        // mecânico achou problema novo, volta pra aprovação
        os.aguardarAprovacao();
        assertEquals(StatusOS.AGUARDANDO_APROVACAO, os.getStatus());
    }

    @Test
    @Story("Rejeitar transição de estado inválida")
    void naoDevePermitirTransicaoInvalida() {
        var os = OrdemDeServico.criar(cliente, veiculo);

        // não pode pular direto pra execução
        assertThrows(IllegalStateException.class, os::aprovarEIniciarExecucao);
    }

    @Test
    @Story("Rejeitar cancelamento de OS finalizada")
    void naoDeveCancelarOSFinalizada() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.avancarParaDiagnostico();
        os.aguardarAprovacao();
        os.aprovarEIniciarExecucao();
        os.finalizar();

        assertThrows(IllegalStateException.class, os::cancelar);
    }

    @Test
    @Story("Cancelar OS no estado RECEBIDA")
    void deveCancelarOSRecebida() {
        var os = OrdemDeServico.criar(cliente, veiculo);
        os.cancelar();
        assertEquals(StatusOS.CANCELADA, os.getStatus());
    }
}
