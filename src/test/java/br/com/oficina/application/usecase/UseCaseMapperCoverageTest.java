package br.com.oficina.application.usecase;

import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.Diagnostico;
import br.com.oficina.domain.model.Encerramento;
import br.com.oficina.domain.model.Entrega;
import br.com.oficina.domain.model.Execucao;
import br.com.oficina.domain.model.Orcamento;
import br.com.oficina.domain.model.Pagamento;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.model.Servico;
import br.com.oficina.domain.model.Usuario;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.MetodoPagamento;
import br.com.oficina.domain.valueobject.PerfilUsuario;
import br.com.oficina.domain.valueobject.Placa;
import br.com.oficina.domain.valueobject.Quantidade;
import br.com.oficina.domain.valueobject.StatusDiagnostico;
import br.com.oficina.domain.valueobject.StatusEntrega;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UseCaseMapperCoverageTest {

    @Test
    void mappersConvertemDominioParaResults() throws Exception {
        var cliente = Cliente.builder()
                .id(1L)
                .documento(new CpfCnpj("52998224725"))
                .nome("Maria")
                .telefone("11999998888")
                .email("maria@email.com")
                .cep("01001-000")
                .logradouro("Praca da Se")
                .bairro("Se")
                .cidade("Sao Paulo")
                .uf("SP")
                .build();
        var veiculo = Veiculo.builder()
                .id(2L)
                .placa(new Placa("ABC1D23"))
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2024)
                .cliente(cliente)
                .build();
        var veiculoComClienteId = Veiculo.builder()
                .id(9L)
                .placa(new Placa("DEF4G56"))
                .marca("Honda")
                .modelo("Civic")
                .ano(2023)
                .clienteId(10L)
                .build();
        var peca = Peca.builder()
                .id(3L)
                .nome("Filtro")
                .descricao("Filtro de oleo")
                .quantidadeEstoque(new Quantidade(1))
                .valorUnitario(new BigDecimal("30.00"))
                .estoqueMinimo(2)
                .build();
        var pagamento = Pagamento.criar(4L, new BigDecimal("100.00"), MetodoPagamento.PIX);
        var entrega = Entrega.builder()
                .id(5L)
                .ordemDeServicoId(4L)
                .status(StatusEntrega.VEICULO_ENTREGUE)
                .dataEntrega(LocalDateTime.now())
                .observacoes("Retirado")
                .build();
        var execucao = Execucao.builder()
                .id(6L)
                .ordemDeServicoId(4L)
                .diagnostico(Diagnostico.builder()
                        .id(7L)
                        .descricaoProblema("Barulho")
                        .dataDiagnostico(LocalDateTime.now())
                        .status(StatusDiagnostico.CONCLUIDO)
                        .build())
                .build();

        assertEquals("Maria", ClienteResultMapper.toResult(cliente).nome());
        assertEquals(1L, VeiculoResultMapper.toResult(veiculo, cliente).clienteId());
        assertEquals(10L, VeiculoResultMapper.toResult(veiculoComClienteId, null).clienteId());
        assertEquals("Filtro", PecaResultMapper.toResult(peca).nome());
        assertEquals("Troca", ServicoResultMapper.toResult(new Servico("Troca", "Oleo", BigDecimal.TEN, 30)).nome());
        assertEquals("PENDENTE", OrcamentoResultMapper.toResult(Orcamento.gerar(4L, BigDecimal.TEN)).status());
        assertEquals("PIX", PagamentoResultMapper.toResult(pagamento).metodo());
        assertEquals("VEICULO_ENTREGUE", EntregaResultMapper.toResult(entrega).status());
        assertEquals("PENDENTE", EncerramentoResultMapper.toResult(Encerramento.criar(4L)).status());
        assertEquals("CONCLUIDO", ExecucaoResultMapper.toResult(execucao).statusDiagnostico());
        assertEquals("ATENDENTE", UsuarioResultMapper.toResult(new Usuario(8L, "user", "hash", PerfilUsuario.ATENDENTE)).role());

        for (var mapper : new Class<?>[] {
                ClienteResultMapper.class,
                EncerramentoResultMapper.class,
                EntregaResultMapper.class,
                ExecucaoResultMapper.class,
                OrcamentoResultMapper.class,
                PagamentoResultMapper.class,
                PecaResultMapper.class,
                ServicoResultMapper.class,
                UsuarioResultMapper.class,
                VeiculoResultMapper.class
        }) {
            var constructor = mapper.getDeclaredConstructor();
            assertEquals(Modifier.PRIVATE, constructor.getModifiers() & Modifier.PRIVATE);
            constructor.setAccessible(true);
            assertNotNull(constructor.newInstance());
        }
    }
}
