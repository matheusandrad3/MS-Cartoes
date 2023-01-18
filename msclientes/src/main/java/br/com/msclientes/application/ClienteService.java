package br.com.msclientes.application;

import br.com.msclientes.domain.Cliente;
import br.com.msclientes.infra.repositoy.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente save (Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> getByCpf (String cpf){
        return clienteRepository.findByCpf(cpf);
    }
}


