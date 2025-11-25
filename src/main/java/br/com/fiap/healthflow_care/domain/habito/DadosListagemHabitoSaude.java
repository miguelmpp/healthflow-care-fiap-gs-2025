package br.com.fiap.healthflow_care.domain.habito;

import java.time.LocalDate;

public record DadosListagemHabitoSaude(
        Long id,
        Long usuarioId,
        TipoHabito tipoHabito,
        LocalDate dataRegistro,
        Integer duracaoMinutos,
        Integer nivelEstresse
) {
    public DadosListagemHabitoSaude(HabitoSaude habito) {
        this(
                habito.getId(),
                habito.getUsuario().getId(),
                habito.getTipoHabito(),
                habito.getDataRegistro(),
                habito.getDuracaoMinutos(),
                habito.getNivelEstresse()
        );
    }
}
