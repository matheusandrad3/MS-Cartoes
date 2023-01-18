package br.com.msavaliadorcredito.application.exception;

public class ErrorSolicitacaoCartaoException extends RuntimeException{
    public ErrorSolicitacaoCartaoException(String message) {
        super(message);
    }
}
