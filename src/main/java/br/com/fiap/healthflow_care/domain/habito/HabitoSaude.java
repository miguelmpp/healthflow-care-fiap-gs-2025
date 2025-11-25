package br.com.fiap.healthflow_care.domain.habito;

import br.com.fiap.healthflow_care.domain.usuario.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;

@Table(name = "habitos_saude")
@Entity(name = "HabitoSaude")
public class HabitoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habito")
    private TipoHabito tipoHabito;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(name = "nivel_cansaco")
    private Integer nivelCansaco;

    @Column(name = "nivel_estresse")
    private Integer nivelEstresse;

    private String observacoes;

    public HabitoSaude() {
    }

    public HabitoSaude(Long id, Boolean ativo, Usuario usuario, TipoHabito tipoHabito,
                       LocalDate dataRegistro, Integer duracaoMinutos,
                       Integer nivelCansaco, Integer nivelEstresse,
                       String observacoes) {
        this.id = id;
        this.ativo = ativo;
        this.usuario = usuario;
        this.tipoHabito = tipoHabito;
        this.dataRegistro = dataRegistro;
        this.duracaoMinutos = duracaoMinutos;
        this.nivelCansaco = nivelCansaco;
        this.nivelEstresse = nivelEstresse;
        this.observacoes = observacoes;
    }

    public HabitoSaude(Usuario usuario, DadosCadastroHabitoSaude dados) {
        this.ativo = true;
        this.usuario = usuario;
        this.tipoHabito = dados.tipoHabito();
        this.dataRegistro = dados.dataRegistro();
        this.duracaoMinutos = dados.duracaoMinutos();
        this.nivelCansaco = dados.nivelCansaco();
        this.nivelEstresse = dados.nivelEstresse();
        this.observacoes = dados.observacoes();
    }

    // ===== Getters =====

    public Long getId() {
        return id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoHabito getTipoHabito() {
        return tipoHabito;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public Integer getNivelCansaco() {
        return nivelCansaco;
    }

    public Integer getNivelEstresse() {
        return nivelEstresse;
    }

    public String getObservacoes() {
        return observacoes;
    }

    // ===== Regras de negócio =====

    public void atualizarInformacoes(DadosAtualizacaoHabitoSaude dados) {
        if (dados.tipoHabito() != null) {
            this.tipoHabito = dados.tipoHabito();
        }
        if (dados.dataRegistro() != null) {
            this.dataRegistro = dados.dataRegistro();
        }
        if (dados.duracaoMinutos() != null) {
            this.duracaoMinutos = dados.duracaoMinutos();
        }
        if (dados.nivelCansaco() != null) {
            this.nivelCansaco = dados.nivelCansaco();
        }
        if (dados.nivelEstresse() != null) {
            this.nivelEstresse = dados.nivelEstresse();
        }
        if (dados.observacoes() != null) {
            this.observacoes = dados.observacoes();
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
