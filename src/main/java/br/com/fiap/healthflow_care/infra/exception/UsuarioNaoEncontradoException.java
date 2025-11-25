package br.com.fiap.healthflow_care.infra.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário com id " + id + " não foi encontrado.");
    }

    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
