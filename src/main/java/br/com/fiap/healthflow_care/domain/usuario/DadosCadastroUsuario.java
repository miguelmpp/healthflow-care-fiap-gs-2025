package br.com.fiap.healthflow_care.domain.usuario;

import br.com.fiap.healthflow_care.domain.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record DadosCadastroUsuario(

        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "\\d{8,15}")
        String telefone,

        @NotNull
        LocalDate dataNascimento,

        @NotNull
        Genero genero,

        @NotNull
        @Valid
        DadosEndereco endereco
) {
}
