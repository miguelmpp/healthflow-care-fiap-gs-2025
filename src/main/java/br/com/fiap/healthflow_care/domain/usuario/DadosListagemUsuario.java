package br.com.fiap.healthflow_care.domain.usuario;

import java.time.LocalDate;

public record DadosListagemUsuario(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        Genero genero
) {
    public DadosListagemUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataNascimento(),
                usuario.getGenero()
        );
    }
}