package br.com.fiap.healthflow_care.domain.usuario;

import br.com.fiap.healthflow_care.domain.endereco.Endereco;
import jakarta.persistence.*;

import java.time.LocalDate;

@Table(name = "usuarios")
@Entity(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean ativo;

    private String nome;

    private String email;

    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Embedded
    private Endereco endereco;

    // Construtor padrão exigido pelo JPA
    public Usuario() {
    }

    // Construtor completo (opcional)
    public Usuario(Long id, Boolean ativo, String nome, String email, String telefone,
                   LocalDate dataNascimento, Genero genero, Endereco endereco) {
        this.id = id;
        this.ativo = ativo;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.endereco = endereco;
    }

    // ✅ Construtor usado pelo Controller: new Usuario(dados)
    public Usuario(DadosCadastroUsuario dados) {
        this.ativo = true;
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.dataNascimento = dados.dataNascimento();
        this.genero = dados.genero();
        this.endereco = new Endereco(dados.endereco());
    }

    // ===== Getters =====

    public Long getId() {
        return id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Genero getGenero() {
        return genero;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    // ===== Regras de negócio =====

    public void atualizarInformacoes(DadosAtualizacaoUsuario dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if (dados.endereco() != null) {
            this.endereco.atualizarInformacoes(dados.endereco());
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
