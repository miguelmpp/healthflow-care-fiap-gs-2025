package br.com.fiap.healthflow_care.repository;

import br.com.fiap.healthflow_care.domain.habito.HabitoSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitoSaudeRepository extends JpaRepository<HabitoSaude, Long> {

    Page<HabitoSaude> findAllByAtivoTrue(Pageable paginacao);

    Page<HabitoSaude> findByUsuarioIdAndAtivoTrue(Long usuarioId, Pageable paginacao);

    // ✅ Para calcular o resumo sem paginação
    List<HabitoSaude> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);
}
