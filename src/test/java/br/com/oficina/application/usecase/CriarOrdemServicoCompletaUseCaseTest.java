package br.com.oficina.application.usecase;

import br.com.oficina.application.command.CriarOrdemServicoCompletaCommand;
import br.com.oficina.application.command.ItemOSCommand;
import br.com.oficina.application.exception.NegocioException;
import br.com.oficina.application.port.in.NotificarStatusOrdemServicoInputPort;
import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.port.out.ClienteRepositoryPort;
import br.com.oficina.application.port.out.ExecucaoRepositoryPort;
import br.com.oficina.application.port.out.OrdemDeServicoRepositoryPort;
import br.com.oficina.application.port.out.PecaRepositoryPort;
import br.com.oficina.application.port.out.ServicoRepositoryPort;
import br.com.oficina.application.port.out.VeiculoRepositoryPort;
import br.com.oficina.domain.model.Cliente;
import br.com.oficina.domain.model.OrdemDeServico;
import br.com.oficina.domain.model.Peca;
import br.com.oficina.domain.model.Servico;
import br.com.oficina.domain.model.Veiculo;
import br.com.oficina.domain.valueobject.CpfCnpj;
import br.com.oficina.domain.valueobject.Placa;
import br.com.oficina.domain.valueobject.Quantidade;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarOrdemServicoCompletaUseCaseTest {

    @Mock private OrdemDeServicoRepositoryPort osRepository;
    @Mock private ClienteRepositoryPort clienteRepository;
    @Mock private VeiculoRepositoryPort veiculoRepository;
    @Mock private ServicoRepositoryPort servicoRepository;
    @Mock private PecaRepositoryPort pecaRepository;
    @Mock private ExecucaoRepositoryPort execucaoRepository;
    @Mock private BuscarEnderecoPorCepPort buscarEnderecoPorCepPort;
    @Mock private NotificarStatusOrdemServicoInputPort notificarStatusOrdemServicoInputPort;

    private CriarOrdemServicoCompletaUseCase useCase;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setup() {
        useCase = new CriarOrdemServicoCompletaUseCase(
            osRepository,
            clienteRepository,
            veiculoRepository,
            servicoRepository,
            pecaRepository,
            execucaoRepository,
            buscarEnderecoPorCepPort,
            notificarStatusOrdemServicoInputPort
        );

        cliente = Cliente.builder()
            .id(1L)
            .documento(new CpfCnpj("52998224725"))
            .nome("Maria")
            .telefone("11999999999")
            .email("maria@email.com")
            .build();
        veiculo = Veiculo.builder()
            .id(2L)
            .placa(new Placa("ABC1D23"))
            .marca("Honda")
            .modelo("Civic")
            .ano(2020)
            .clienteId(1L)
            .cliente(cliente)
            .build();
    }

    @Test
    void deveCriarOSCompletaComClienteEVeiculoNovos() {
        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.empty());
        when(clienteRepository.salvar(any())).thenReturn(cliente);
        when(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(Optional.empty());
        when(veiculoRepository.salvar(any())).thenReturn(veiculo);
        when(servicoRepository.buscarPorId(10L)).thenReturn(Optional.of(servico()));
        when(pecaRepository.buscarPorId(20L)).thenReturn(Optional.of(peca()));
        mockSalvarOS();
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(2L)).thenReturn(Optional.of(veiculo));

        var result = useCase.execute(command());

        assertEquals("OS-" + result.dataCriacao().getYear() + "-00001", result.numero());
        assertEquals("RECEBIDA", result.status());
        assertEquals(2, result.itens().size());
        verify(execucaoRepository).salvar(any());
    }

    @Test
    void deveReaproveitarClienteEVeiculoExistentes() {
        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(Optional.of(veiculo));
        mockSalvarOS();
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(2L)).thenReturn(Optional.of(veiculo));

        var result = useCase.execute(commandSemItens());

        assertEquals("RECEBIDA", result.status());
        verify(clienteRepository, never()).salvar(any());
        verify(veiculoRepository, never()).salvar(any());
    }

    @Test
    void deveRejeitarVeiculoVinculadoAOutroCliente() {
        var outroVeiculo = Veiculo.builder()
            .id(2L)
            .placa(new Placa("ABC1D23"))
            .marca("Honda")
            .modelo("Civic")
            .ano(2020)
            .clienteId(99L)
            .build();

        when(clienteRepository.buscarPorDocumento("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(Optional.of(outroVeiculo));

        assertThrows(NegocioException.class, () -> useCase.execute(commandSemItens()));
        verify(osRepository, never()).salvar(any());
    }

    private CriarOrdemServicoCompletaCommand command() {
        return new CriarOrdemServicoCompletaCommand(
            "52998224725",
            "Maria",
            "11999999999",
            "maria@email.com",
            "01001000",
            "Praca da Se",
            "Se",
            "Sao Paulo",
            "SP",
            "ABC1D23",
            "Honda",
            "Civic",
            2020,
            List.of(new ItemOSCommand("SERVICO", 10L, 1), new ItemOSCommand("PECA", 20L, 2)),
            "atendente"
        );
    }

    private CriarOrdemServicoCompletaCommand commandSemItens() {
        var command = command();
        return new CriarOrdemServicoCompletaCommand(
            command.documento(),
            command.nome(),
            command.telefone(),
            command.email(),
            command.cep(),
            command.logradouro(),
            command.bairro(),
            command.cidade(),
            command.uf(),
            command.placa(),
            command.marca(),
            command.modelo(),
            command.ano(),
            List.of(),
            command.atendenteNome()
        );
    }

    private Servico servico() {
        return Servico.builder()
            .id(10L)
            .nome("Troca de oleo")
            .descricao("Troca de oleo")
            .valorUnitario(new BigDecimal("120.00"))
            .tempoEstimadoMinutos(60)
            .build();
    }

    private Peca peca() {
        return Peca.builder()
            .id(20L)
            .nome("Filtro")
            .descricao("Filtro de oleo")
            .quantidadeEstoque(new Quantidade(10))
            .valorUnitario(new BigDecimal("35.00"))
            .build();
    }

    private void mockSalvarOS() {
        when(osRepository.salvar(any())).thenAnswer(invocation -> {
            var os = (OrdemDeServico) invocation.getArgument(0);
            if (os.getId() == null) {
                os.atribuirId(1L);
            }
            return os;
        });
    }
}
