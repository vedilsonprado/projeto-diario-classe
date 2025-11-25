package com.diarioclasse.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.TurmaUC;

@Repository
public interface TurmaUCRepository extends JpaRepository<TurmaUC, Long> {
    
    // Método para buscar todas as UCs de uma Turma específica
    List<TurmaUC> findByTurmaId(Long turmaId);

    // Método para buscar todas as Turmas que cursam uma UC específica
    List<TurmaUC> findByUnidadeCurricularId(Long ucId);

    // Método para checar se uma Turma já está associada a uma UC específica
    Optional<TurmaUC> findByTurmaIdAndUnidadeCurricularId(Long turmaId, Long ucId);
}
