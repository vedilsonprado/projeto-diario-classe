package com.diarioclasse.api.servicies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.Turma;
import com.diarioclasse.api.repositories.TurmaRepository;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    /**
     * Cadastra uma nova Turma, garantindo que o nomeTurma não seja duplicado.
     */
    public Turma cadastrar(Turma turma) {
        if (turmaRepository.findByNomeTurma(turma.getNomeTurma()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Já existe uma turma cadastrada com este nome: " + turma.getNomeTurma()
            );
        }
        return turmaRepository.save(turma);
    }

    /**
     * Lista todas as Turmas cadastradas.
     */
    public List<Turma> listarTodos() {
        return turmaRepository.findAll();
    }

    /**
     * Busca uma Turma pelo ID.
     */
    public Turma buscarPorId(Long id) {
        return turmaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Turma não encontrada para o ID: " + id
            ));
    }

    /**
     * Atualiza os dados de uma Turma existente.
     */
    public Turma atualizar(Long id, Turma turmaDetalhes) {
        Turma turmaExistente = buscarPorId(id); // Reusa o método de busca
        
        turmaExistente.setNomeTurma(turmaDetalhes.getNomeTurma());
        turmaExistente.setAnoSemestre(turmaDetalhes.getAnoSemestre());
        
        return turmaRepository.save(turmaExistente);
    }

    /**
     * Deleta uma Turma pelo ID.
     */
    public void deletar(Long id) {
        Turma turmaExistente = buscarPorId(id);
        turmaRepository.delete(turmaExistente);
    }
}
