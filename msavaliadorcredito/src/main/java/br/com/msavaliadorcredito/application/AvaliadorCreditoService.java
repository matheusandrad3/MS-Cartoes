package br.com.msavaliadorcredito.application;

import br.com.msavaliadorcredito.application.exception.DadosClienteNotFoudException;
import br.com.msavaliadorcredito.application.exception.ErroComunicacaoMicroservicesException;
import br.com.msavaliadorcredito.application.exception.ErrorSolicitacaoCartaoException;
import br.com.msavaliadorcredito.domain.model.*;
import br.com.msavaliadorcredito.infra.clients.CartoesResourceClient;
import br.com.msavaliadorcredito.infra.clients.ClienteResourceClient;
import br.com.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clietesClient;
    private final CartoesResourceClient cartaoClient;

    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;


    public SitucaoCliente obterSituacaoCliente(String cpf)
            throws DadosClienteNotFoudException, ErroComunicacaoMicroservicesException {

        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clietesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesByClient = cartaoClient.getCartoesByCliente(cpf);

            return SitucaoCliente.builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesByClient.getBody())
                    .build();
        }catch (FeignException.FeignClientException e){
          int status = e.status();
          if (HttpStatus.NOT_FOUND.value() == status){
            throw  new DadosClienteNotFoudException();
          }
          throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
        }
    }

    public RetornoAvaliacaoCliente retornoAvaliacaoCliente(String cpf, Long renda) throws DadosClienteNotFoudException, ErroComunicacaoMicroservicesException{

        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clietesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartaoClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            List<CartaoAprovado> listaCartaoAprovado = cartoes.stream().map(cartao -> {

                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado cartaoAprovado = new CartaoAprovado();
                cartaoAprovado.setNome(cartao.getNome());
                cartaoAprovado.setBandeira(cartao.getBandeira());
                cartaoAprovado.setLimiteAprovado(limiteAprovado);

                return cartaoAprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartaoAprovado);

        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status){
                throw  new DadosClienteNotFoudException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
        }

    }

    public ProtocoloSolitacaoCartao solicitarEmissaoCartao(DadosSolictacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            String protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolitacaoCartao(protocolo);
        }catch (Exception e){
                throw new ErrorSolicitacaoCartaoException(e.getMessage());
        }
    }

}
