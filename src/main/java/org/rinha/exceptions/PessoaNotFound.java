package org.rinha.exceptions;

public class PessoaNotFound extends RuntimeException {
    public PessoaNotFound(String message) {
        super(message);
    }
}
