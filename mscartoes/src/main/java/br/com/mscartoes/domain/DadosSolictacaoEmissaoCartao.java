package br.com.mscartoes.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosSolictacaoEmissaoCartao {
    private Long idCartao;
    private String cpf;
    private String endereco;
    private BigDecimal limiteLiberado;
}
