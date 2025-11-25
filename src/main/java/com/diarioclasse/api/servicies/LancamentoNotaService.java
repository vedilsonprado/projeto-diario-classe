package com.diarioclasse.api.servicies;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.Aluno;
import com.diarioclasse.api.entities.Criterio;
import com.diarioclasse.api.entities.LancamentoNota;
import com.diarioclasse.api.repositories.LancamentoNotaRepository;

import jakarta.transaction.Transactional;

@Service
public class LancamentoNotaService {

    @Autowired
    private LancamentoNotaRepository lancamentoNotaRepository;

    @Autowired
    private AlunoService alunoService; 
    
    @Autowired
    private CriterioService criterioService; 

    /**
     * Cadastra ou atualiza o lançamento (Boolean Atingiu/Não Atingiu).
     */
    @Transactional
    public LancamentoNota lancarOuAtualizarNota(LancamentoNota lancamento) {
        Long alunoId = lancamento.getAluno().getId();
        Long criterioId = lancamento.getCriterio().getId();
        
        // 1. Busca e Valida Entidades
        Aluno alunoExistente = alunoService.buscarPorId(alunoId);
        Criterio criterioExistente = criterioService.buscarPorId(criterioId);
        
        // (REMOVIDO: Validação de Nota Máxima, pois agora é Boolean)

        // 2. Verifica se é um lançamento novo ou atualização
        Optional<LancamentoNota> lancamentoExistente = 
            lancamentoNotaRepository.findByAlunoIdAndCriterioId(alunoId, criterioId);

        LancamentoNota notaParaSalvar;

        if (lancamentoExistente.isPresent()) {
            // Atualização
            notaParaSalvar = lancamentoExistente.get();
            // Atualiza o valor Booleano (Atingiu ou Não)
            notaParaSalvar.setAtingiu(lancamento.getAtingiu());
        } else {
            // Novo Lançamento
            lancamento.setAluno(alunoExistente);
            lancamento.setCriterio(criterioExistente);
            notaParaSalvar = lancamento;
        }

        return lancamentoNotaRepository.save(notaParaSalvar);
    }

    public List<LancamentoNota> listarPorAluno(Long alunoId) {
        alunoService.buscarPorId(alunoId); 
        return lancamentoNotaRepository.findByAlunoId(alunoId);
    }

    public void deletar(Long id) {
        LancamentoNota lancamentoExistente = lancamentoNotaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Lançamento de nota não encontrado para o ID: " + id
            ));
        lancamentoNotaRepository.delete(lancamentoExistente);
    }
}