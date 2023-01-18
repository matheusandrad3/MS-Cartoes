package br.com.msavaliadorcredito.application;

import br.com.msavaliadorcredito.application.exception.DadosClienteNotFoudException;
import br.com.msavaliadorcredito.application.exception.ErroComunicacaoMicroservicesException;
import br.com.msavaliadorcredito.application.exception.ErrorSolicitacaoCartaoException;
import br.com.msavaliadorcredito.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status(){
    return "ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultaSituacaoCliente(@RequestParam("cpf") String cpf){
        try {
            SitucaoCliente situcaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situcaoCliente);
        } catch (DadosClienteNotFoudException e) {
           return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroservicesException e) {
         return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity realizarAvaliacao (@RequestBody DadosAvaliacao dados){
        try {
           RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.retornoAvaliacaoCliente(dados.getCpf(), dados.getRenda());
           return ResponseEntity.ok(retornoAvaliacaoCliente);
        } catch (DadosClienteNotFoudException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroservicesException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolictacaoEmissaoCartao dados){
        try{
            ProtocoloSolitacaoCartao protocoloSolitacaoCartao = avaliadorCreditoService.solicitarEmissaoCartao(dados);
            return ResponseEntity.ok(protocoloSolitacaoCartao);
        }catch (ErrorSolicitacaoCartaoException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
