package br.com.fiap.healthflow_care.domain.habito;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DadosAtualizacaoHabitoSaude(

        @NotNull
        Long id,

        TipoHabito tipoHabito,
        LocalDate dataRegistro,

        @Min(1)
        @Max(1440)
        Integer duracaoMinutos,

        @Min(0)
        @Max(10)
        Integer nivelCansaco,

        @Min(0)
        @Max(10)
        Integer nivelEstresse,

        @Size(max = 255)
        String observacoes
) {
}
