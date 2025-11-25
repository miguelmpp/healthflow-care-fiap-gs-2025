package br.com.fiap.healthflow_care.domain.usuario;

import br.com.fiap.healthflow_care.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoUsuario(

        @NotNull
        Long id,

        String nome,
        String telefone,

        @Valid
        DadosEndereco endereco
) {
}
