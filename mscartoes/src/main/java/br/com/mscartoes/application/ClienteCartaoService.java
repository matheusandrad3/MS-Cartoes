package br.com.mscartoes.application;

import br.com.mscartoes.domain.ClienteCartao;
import br.com.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {

    private final ClienteCartaoRepository clienteCartaoRepository;

    public List<ClienteCartao> findByCpf(String cpf){
        return clienteCartaoRepository.findByCpf(cpf);
    }
}
