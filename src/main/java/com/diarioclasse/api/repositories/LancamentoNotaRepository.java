package com.diarioclasse.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.LancamentoNota;

@Repository
public interface LancamentoNotaRepository extends JpaRepository<LancamentoNota, Long> {
    
    // Método crucial para a tela do diário: busca todos os lançamentos de um aluno
    List<LancamentoNota> findByAlunoId(Long alunoId);

    // Método para buscar lançamentos de todos os alunos que estão sendo avaliados por um critério específico
    List<LancamentoNota> findByCriterioId(Long criterioId);

    // Método para buscar um lançamento específico (se já existe)
    Optional<LancamentoNota> findByAlunoIdAndCriterioId(Long alunoId, Long criterioId);
}
