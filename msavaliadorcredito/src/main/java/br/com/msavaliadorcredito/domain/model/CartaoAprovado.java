package br.com.msavaliadorcredito.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartaoAprovado {
    private String nome;
    private String bandeira;
    private BigDecimal limiteAprovado;
}
