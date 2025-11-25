package com.diarioclasse.api.servicies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.Criterio;
import com.diarioclasse.api.entities.UnidadeCurricular;
import com.diarioclasse.api.repositories.CriterioRepository;

@Service
public class CriterioService {

    @Autowired
    private CriterioRepository criterioRepository;

    @Autowired
    private UnidadeCurricularService ucService;

    public Criterio cadastrar(Criterio criterio) {
        Long ucId = criterio.getUnidadeCurricular().getId();
        UnidadeCurricular ucExistente = ucService.buscarPorId(ucId);
        
        criterio.setUnidadeCurricular(ucExistente); 
        
        return criterioRepository.save(criterio);
    }

    public List<Criterio> listarPorUnidadeCurricular(Long ucId) {
        ucService.buscarPorId(ucId); 
        return criterioRepository.findByUnidadeCurricularId(ucId);
    }
    
    public List<Criterio> listarTodos() {
        return criterioRepository.findAll();
    }

    public Criterio buscarPorId(Long id) {
        return criterioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Critério de avaliação não encontrado para o ID: " + id
            ));
    }

    public Criterio atualizar(Long id, Criterio criterioDetalhes) {
        Criterio criterioExistente = buscarPorId(id);
        
        // Atualiza campos (REMOVIDOS PESO E NOTA MÁXIMA)
        criterioExistente.setDescricao(criterioDetalhes.getDescricao());
        criterioExistente.setTipoAvaliacao(criterioDetalhes.getTipoAvaliacao());
        criterioExistente.setTipoCapacidade(criterioDetalhes.getTipoCapacidade());

        // Atualiza UC se necessário
        if (criterioDetalhes.getUnidadeCurricular() != null && criterioDetalhes.getUnidadeCurricular().getId() != null) {
            UnidadeCurricular novaUc = ucService.buscarPorId(criterioDetalhes.getUnidadeCurricular().getId());
            criterioExistente.setUnidadeCurricular(novaUc);
        }
        
        return criterioRepository.save(criterioExistente);
    }

    public void deletar(Long id) {
        Criterio criterioExistente = buscarPorId(id);
        criterioRepository.delete(criterioExistente);
    }
}