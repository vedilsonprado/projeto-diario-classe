package com.diarioclasse.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.Anotacao;

@Repository
public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {
    // Busca ordenando da mais recente para a mais antiga
    List<Anotacao> findByAlunoIdOrderByDataHoraDesc(Long alunoId);
}