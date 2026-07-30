package br.com.oficina.adapters.out.viacep;

import br.com.oficina.application.port.out.BuscarEnderecoPorCepPort;
import br.com.oficina.application.query.EnderecoResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViaCepEnderecoAdapter implements BuscarEnderecoPorCepPort {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;

    @Override
    public Optional<EnderecoResult> buscarPorCep(String cep) {
        String cepLimpo = cep.replaceAll("[^\\d]", "");
        if (cepLimpo.length() != 8) {
            return Optional.empty();
        }
        try {
            var response = restTemplate.getForObject(VIA_CEP_URL, ViaCepEnderecoResponse.class, cepLimpo);
            if (response == null || Boolean.TRUE.equals(response.erro())) {
                return Optional.empty();
            }
            return Optional.of(new EnderecoResult(
                response.cep(),
                response.logradouro(),
                response.bairro(),
                response.localidade(),
                response.uf()
            ));
        } catch (RestClientException e) {
            log.warn("Erro ao consultar ViaCEP para CEP {}: {}", cepLimpo, e.getMessage());
            return Optional.empty();
        }
    }
}
