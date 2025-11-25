package br.com.fiap.healthflow_care.controller;

import br.com.fiap.healthflow_care.domain.usuario.DadosAtualizacaoUsuario;
import br.com.fiap.healthflow_care.domain.usuario.DadosCadastroUsuario;
import br.com.fiap.healthflow_care.domain.usuario.DadosListagemUsuario;
import br.com.fiap.healthflow_care.domain.usuario.Usuario;
import br.com.fiap.healthflow_care.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        repository.save(new Usuario(dados));
    }

    @GetMapping
    public Page<DadosListagemUsuario> listar(
            @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {

        return repository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemUsuario::new);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        Usuario usuario = repository.getReferenceById(dados.id());
        usuario.atualizarInformacoes(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {
        Usuario usuario = repository.getReferenceById(id);
        usuario.excluir();
    }
}
