package br.com.oficina.adapters.in.web.mapper;

import br.com.oficina.adapters.in.web.request.CriarOrdemServicoCompletaRequest;
import br.com.oficina.adapters.in.web.request.CriarOrdemServicoRequest;
import br.com.oficina.adapters.in.web.request.ItemOSRequest;
import br.com.oficina.adapters.in.web.response.OrcamentoResponse;
import br.com.oficina.adapters.in.web.response.OrdemServicoResponse;
import br.com.oficina.application.command.AdicionarItemOSCommand;
import br.com.oficina.application.command.CriarOrdemServicoCompletaCommand;
import br.com.oficina.application.command.CriarOrdemServicoCommand;
import br.com.oficina.application.command.ItemOSCommand;
import br.com.oficina.application.query.OrcamentoResult;
import br.com.oficina.application.query.OrdemServicoResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrdemDeServicoWebMapper {

    public CriarOrdemServicoCommand toCriarCommand(CriarOrdemServicoRequest request, String atendenteNome) {
        return new CriarOrdemServicoCommand(
            request.clienteId(),
            request.veiculoId(),
            toItemCommands(request.itens()),
            atendenteNome
        );
    }

    public CriarOrdemServicoCompletaCommand toCriarCompletaCommand(
            CriarOrdemServicoCompletaRequest request,
            String atendenteNome) {
        var cliente = request.cliente();
        var veiculo = request.veiculo();
        return new CriarOrdemServicoCompletaCommand(
            cliente.documento(),
            cliente.nome(),
            cliente.telefone(),
            cliente.email(),
            cliente.cep(),
            cliente.logradouro(),
            cliente.bairro(),
            cliente.cidade(),
            cliente.uf(),
            veiculo.placa(),
            veiculo.marca(),
            veiculo.modelo(),
            veiculo.ano(),
            toItemCommands(request),
            atendenteNome
        );
    }

    public AdicionarItemOSCommand toAdicionarItensCommand(Long ordemServicoId, List<ItemOSRequest> itens) {
        return new AdicionarItemOSCommand(ordemServicoId, toItemCommands(itens));
    }

    public OrdemServicoResponse toResponse(OrdemServicoResult result) {
        return new OrdemServicoResponse(
            result.id(),
            result.numero(),
            result.status(),
            result.dataCriacao(),
            result.dataFinalizacao(),
            result.clienteNome(),
            result.clienteDocumento(),
            result.veiculoPlaca(),
            result.veiculoDescricao(),
            result.itens().stream()
                .map(i -> new OrdemServicoResponse.ItemOSResponse(
                    i.id(), i.tipo(), i.descricao(), i.quantidade(), i.valorUnitario(), i.subtotal()))
                .toList(),
            result.valorTotal(),
            result.atendenteNome(),
            result.mecanicoNome()
        );
    }

    public OrcamentoResponse toResponse(OrcamentoResult result) {
        return new OrcamentoResponse(
            result.id(),
            result.ordemServicoId(),
            result.status(),
            result.valorTotal(),
            result.dataCriacao(),
            result.dataValidade()
        );
    }

    private List<ItemOSCommand> toItemCommands(List<ItemOSRequest> itens) {
        if (itens == null) {
            return List.of();
        }
        return itens.stream()
                .map(this::toItemCommand)
                .toList();
    }

    private List<ItemOSCommand> toItemCommands(CriarOrdemServicoCompletaRequest request) {
        var itens = new ArrayList<ItemOSCommand>();
        if (request.servicos() != null) {
            request.servicos().forEach(servico ->
                itens.add(new ItemOSCommand("SERVICO", servico.servicoId(), servico.quantidade()))
            );
        }
        if (request.pecas() != null) {
            request.pecas().forEach(peca ->
                itens.add(new ItemOSCommand("PECA", peca.pecaId(), peca.quantidade()))
            );
        }
        return itens;
    }

    private ItemOSCommand toItemCommand(ItemOSRequest request) {
        return new ItemOSCommand(request.tipo(), request.referenciaId(), request.quantidade());
    }
}
