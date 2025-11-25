package br.com.fiap.healthflow_care.domain.habito;

public record RecomendacaoBemEstarUsuario(
        ResumoBemEstarUsuario resumo,
        String mensagemResumo,
        String dicaExterna
) {
}
