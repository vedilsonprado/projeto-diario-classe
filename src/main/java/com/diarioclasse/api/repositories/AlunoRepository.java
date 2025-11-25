package com.diarioclasse.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Método customizado para buscar alunos por turma (necessário para o diário de classe)
    List<Aluno> findByTurmaId(Long turmaId);

    // Método para garantir que o email é único (opcional, mas recomendado)
    Optional<Aluno> findByEmail(String email);
}
