package com.diarioclasse.api.servicies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.Capacidade;
import com.diarioclasse.api.entities.Criterio;
import com.diarioclasse.api.repositories.CriterioRepository;

@Service
public class CriterioService {

    @Autowired
    private CriterioRepository criterioRepository;

    @Autowired
    private CapacidadeService capacidadeService;
    
    @Autowired
    private UnidadeCurricularService ucService; // Necessário para validar UC

    public Criterio cadastrar(Criterio criterio) {
        Long capacidadeId = criterio.getCapacidade().getId();
        Capacidade capacidadeExistente = capacidadeService.buscarPorId(capacidadeId);
        criterio.setCapacidade(capacidadeExistente); 
        return criterioRepository.save(criterio);
    }

    // --- MÉTODO RESTAURADO (CORRIGE O ERRO DO CONTROLLER) ---
    public List<Criterio> listarPorUnidadeCurricular(Long ucId) {
        // Valida se a UC existe
        ucService.buscarPorId(ucId);
        // Usa a busca aninhada do repositório
        return criterioRepository.findByCapacidadeUnidadeCurricularId(ucId);
    }

    public List<Criterio> listarPorCapacidade(Long capacidadeId) {
        capacidadeService.buscarPorId(capacidadeId);
        return criterioRepository.findByCapacidadeId(capacidadeId);
    }
    
    public List<Criterio> listarTodos() {
        return criterioRepository.findAll();
    }

    public Criterio buscarPorId(Long id) {
        return criterioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Critério não encontrado: " + id
            ));
    }

    public Criterio atualizar(Long id, Criterio criterioDetalhes) {
        Criterio criterioExistente = buscarPorId(id);
        
        criterioExistente.setDescricao(criterioDetalhes.getDescricao());
        criterioExistente.setTipoAvaliacao(criterioDetalhes.getTipoAvaliacao());
        
        if (criterioDetalhes.getCapacidade() != null && criterioDetalhes.getCapacidade().getId() != null) {
            Capacidade novaCapacidade = capacidadeService.buscarPorId(criterioDetalhes.getCapacidade().getId());
            criterioExistente.setCapacidade(novaCapacidade);
        }
        
        return criterioRepository.save(criterioExistente);
    }

    public void deletar(Long id) {
        Criterio criterioExistente = buscarPorId(id);
        criterioRepository.delete(criterioExistente);
    }
}