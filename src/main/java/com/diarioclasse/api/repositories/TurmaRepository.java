package com.diarioclasse.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.Turma;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

	// Método customizado para buscar uma turma pelo nome, se necessário
	Optional<Turma> findByNomeTurma(String nomeTurma);

}
