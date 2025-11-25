package com.diarioclasse.api.servicies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.Turma;
import com.diarioclasse.api.entities.TurmaUC;
import com.diarioclasse.api.entities.UnidadeCurricular;
import com.diarioclasse.api.repositories.TurmaUCRepository;

@Service
public class TurmaUCService {

    @Autowired
    private TurmaUCRepository turmaUcRepository;

    @Autowired
    private TurmaService turmaService; // Dependência da Turma Service
    
    @Autowired
    private UnidadeCurricularService ucService; // Dependência da UC Service

    /**
     * Associa uma Turma a uma Unidade Curricular.
     */
    public TurmaUC associarTurmaUC(TurmaUC associacao) {
        Long turmaId = associacao.getTurma().getId();
        Long ucId = associacao.getUnidadeCurricular().getId();

        // 1. Verifica se a associação já existe
        if (turmaUcRepository.findByTurmaIdAndUnidadeCurricularId(turmaId, ucId).isPresent()) {
             throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "A Turma já está associada a esta Unidade Curricular."
            );
        }

        // 2. Busca e valida as entidades gerenciadas
        Turma turmaExistente = turmaService.buscarPorId(turmaId);
        UnidadeCurricular ucExistente = ucService.buscarPorId(ucId);
        
        // Atribui as entidades gerenciadas
        associacao.setTurma(turmaExistente);
        associacao.setUnidadeCurricular(ucExistente);
        
        return turmaUcRepository.save(associacao);
    }

    /**
     * Lista todas as associações de uma Turma específica.
     */
    public List<TurmaUC> listarPorTurma(Long turmaId) {
        // Apenas para garantir que a turma existe antes de listar
        turmaService.buscarPorId(turmaId); 
        return turmaUcRepository.findByTurmaId(turmaId);
    }

    /**
     * Desfaz a associação (Deleta pelo ID da associação).
     */
    public void desassociar(Long id) {
        TurmaUC associacaoExistente = turmaUcRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Associação Turma-UC não encontrada para o ID: " + id
            ));
        turmaUcRepository.delete(associacaoExistente);
    }
}
