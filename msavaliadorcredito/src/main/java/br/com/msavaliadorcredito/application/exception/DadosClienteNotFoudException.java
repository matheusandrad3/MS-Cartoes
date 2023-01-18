package br.com.msavaliadorcredito.application.exception;

public class DadosClienteNotFoudException extends Exception{
    public DadosClienteNotFoudException() {
        super("Dados do cliente não encontrado");
    }
}
