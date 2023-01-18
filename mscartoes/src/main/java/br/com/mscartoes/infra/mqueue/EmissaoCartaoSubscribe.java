package br.com.mscartoes.infra.mqueue;

import br.com.mscartoes.domain.Cartao;
import br.com.mscartoes.domain.ClienteCartao;
import br.com.mscartoes.domain.DadosSolictacaoEmissaoCartao;
import br.com.mscartoes.infra.repository.CartaoRepository;
import br.com.mscartoes.infra.repository.ClienteCartaoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmissaoCartaoSubscribe {
    private final CartaoRepository cartaoRepository;
    private final ClienteCartaoRepository clienteCartaoRepository;

    @RabbitListener(queues ="${mq.queues.emissao-cartoes}" )
    public void receberSoliciatacaoEmissao(@Payload String payload){
        try{

            ObjectMapper mapper = new ObjectMapper();
            DadosSolictacaoEmissaoCartao dados = mapper.readValue(payload, DadosSolictacaoEmissaoCartao.class);

            Cartao cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();
            ClienteCartao clienteCartao = new ClienteCartao();
            clienteCartao.setCartao(cartao);
            clienteCartao.setCpf(dados.getCpf());
            clienteCartao.setLimite(dados.getLimiteLiberado());

            clienteCartaoRepository.save(clienteCartao);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
