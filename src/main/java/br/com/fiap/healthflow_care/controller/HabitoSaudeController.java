package br.com.fiap.healthflow_care.controller;

import br.com.fiap.healthflow_care.domain.habito.DadosAtualizacaoHabitoSaude;
import br.com.fiap.healthflow_care.domain.habito.DadosCadastroHabitoSaude;
import br.com.fiap.healthflow_care.domain.habito.DadosListagemHabitoSaude;
import br.com.fiap.healthflow_care.domain.habito.HabitoSaude;
import br.com.fiap.healthflow_care.domain.habito.ResumoBemEstarUsuario;
import br.com.fiap.healthflow_care.domain.habito.RecomendacaoBemEstarUsuario;
import br.com.fiap.healthflow_care.domain.usuario.Usuario;
import br.com.fiap.healthflow_care.infra.exception.UsuarioNaoEncontradoException;
import br.com.fiap.healthflow_care.infra.external.AdviceSlipResponse;
import br.com.fiap.healthflow_care.repository.HabitoSaudeRepository;
import br.com.fiap.healthflow_care.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping
public class HabitoSaudeController {

    @Autowired
    private HabitoSaudeRepository habitoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RestTemplate restTemplate;

    // POST /habitos-saude
    @PostMapping("/habitos-saude")
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroHabitoSaude dados) {
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(dados.usuarioId()));

        HabitoSaude habito = new HabitoSaude(usuario, dados);
        habitoRepository.save(habito);
    }

    // GET /habitos-saude  -> todos os hábitos ativos
    @GetMapping("/habitos-saude")
    public Page<DadosListagemHabitoSaude> listar(
            @PageableDefault(size = 10, sort = {"dataRegistro"}) Pageable paginacao) {

        return habitoRepository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemHabitoSaude::new);
    }

    // GET /usuarios/{usuarioId}/habitos-saude -> hábitos por usuário
    @GetMapping("/usuarios/{usuarioId}/habitos-saude")
    public Page<DadosListagemHabitoSaude> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10, sort = {"dataRegistro"}) Pageable paginacao) {

        return habitoRepository.findByUsuarioIdAndAtivoTrue(usuarioId, paginacao)
                .map(DadosListagemHabitoSaude::new);
    }

    // ✅ GET /usuarios/{usuarioId}/resumo-bem-estar
    @GetMapping("/usuarios/{usuarioId}/resumo-bem-estar")
    public ResumoBemEstarUsuario resumoBemEstar(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        List<HabitoSaude> habitos = habitoRepository.findAllByUsuarioIdAndAtivoTrue(usuarioId);

        if (habitos.isEmpty()) {
            return new ResumoBemEstarUsuario(
                    usuario.getId(),
                    usuario.getNome(),
                    0,
                    0, 0, 0, 0, 0,
                    null,
                    null
            );
        }

        int totalHabitos = habitos.size();

        int totalMinutosPausa = 0;
        int totalMinutosSono = 0;
        int totalMinutosExercicio = 0;
        int totalMinutosAlimentacao = 0;
        int totalMinutosFocus = 0;

        int somaEstresse = 0;
        int countEstresse = 0;

        int somaCansaco = 0;
        int countCansaco = 0;

        for (HabitoSaude h : habitos) {
            Integer dur = h.getDuracaoMinutos();
            if (dur == null) {
                dur = 0;
            }

            if (h.getTipoHabito() != null) {
                switch (h.getTipoHabito()) {
                    case PAUSA -> totalMinutosPausa += dur;
                    case SONO -> totalMinutosSono += dur;
                    case EXERCICIO -> totalMinutosExercicio += dur;
                    case ALIMENTACAO -> totalMinutosAlimentacao += dur;
                    case FOCUS -> totalMinutosFocus += dur;
                }
            }

            if (h.getNivelEstresse() != null) {
                somaEstresse += h.getNivelEstresse();
                countEstresse++;
            }

            if (h.getNivelCansaco() != null) {
                somaCansaco += h.getNivelCansaco();
                countCansaco++;
            }
        }

        Double mediaEstresse = countEstresse > 0 ? somaEstresse * 1.0 / countEstresse : null;
        Double mediaCansaco = countCansaco > 0 ? somaCansaco * 1.0 / countCansaco : null;

        return new ResumoBemEstarUsuario(
                usuario.getId(),
                usuario.getNome(),
                totalHabitos,
                totalMinutosPausa,
                totalMinutosSono,
                totalMinutosExercicio,
                totalMinutosAlimentacao,
                totalMinutosFocus,
                mediaEstresse,
                mediaCansaco
        );
    }

    // ✅ GET /usuarios/{usuarioId}/recomendacoes
    // Combina o resumo + chamada à API externa Advice Slip
    @GetMapping("/usuarios/{usuarioId}/recomendacoes")
    public RecomendacaoBemEstarUsuario recomendacoes(@PathVariable Long usuarioId) {
        // Reutiliza o método que calcula o resumo
        ResumoBemEstarUsuario resumo = resumoBemEstar(usuarioId);

        String mensagemResumo = montarMensagemResumo(resumo);

        String dicaExterna;

        try {
            ResponseEntity<AdviceSlipResponse> respostaApi = restTemplate.getForEntity(
                    "https://api.adviceslip.com/advice",
                    AdviceSlipResponse.class
            );

            if (respostaApi.getStatusCode().is2xxSuccessful()
                    && respostaApi.getBody() != null
                    && respostaApi.getBody().slip() != null
                    && respostaApi.getBody().slip().advice() != null) {

                dicaExterna = respostaApi.getBody().slip().advice();
            } else {
                dicaExterna = "Não foi possível obter uma dica externa no momento. Tente novamente mais tarde.";
            }
        } catch (Exception e) {
            dicaExterna = "Falha ao se conectar ao serviço externo de dicas. Verifique sua conexão e tente novamente.";
        }

        return new RecomendacaoBemEstarUsuario(
                resumo,
                mensagemResumo,
                dicaExterna
        );
    }

    private String montarMensagemResumo(ResumoBemEstarUsuario resumo) {
        if (resumo.totalHabitos() == 0) {
            return "Ainda não há hábitos registrados para esse usuário. Comece registrando pausas, sono e exercícios para gerar recomendações personalizadas.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Com base em ")
                .append(resumo.totalHabitos())
                .append(" hábitos registrados, ");

        if (resumo.mediaEstresse() != null) {
            sb.append("o nível médio de estresse está em ")
                    .append(resumo.mediaEstresse())
                    .append(" de 10");
        }

        if (resumo.mediaCansaco() != null) {
            if (resumo.mediaEstresse() != null) {
                sb.append(" e ");
            }
            sb.append("o cansaço médio em ")
                    .append(resumo.mediaCansaco())
                    .append(" de 10");
        }

        sb.append(". Busque equilibrar sono, pausas e exercícios ao longo da semana.");
        return sb.toString();
    }

    // PUT /habitos-saude
    @PutMapping("/habitos-saude")
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoHabitoSaude dados) {
        HabitoSaude habito = habitoRepository.getReferenceById(dados.id());
        habito.atualizarInformacoes(dados);
    }

    // DELETE /habitos-saude/{id} (soft delete)
    @DeleteMapping("/habitos-saude/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {
        HabitoSaude habito = habitoRepository.getReferenceById(id);
        habito.excluir();
    }
}
