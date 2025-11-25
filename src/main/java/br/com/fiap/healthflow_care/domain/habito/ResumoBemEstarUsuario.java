package br.com.fiap.healthflow_care.domain.habito;

public record ResumoBemEstarUsuario(
        Long usuarioId,
        String nome,
        int totalHabitos,
        int totalMinutosPausa,
        int totalMinutosSono,
        int totalMinutosExercicio,
        int totalMinutosAlimentacao,
        int totalMinutosFocus,
        Double mediaEstresse,
        Double mediaCansaco
) {
}
