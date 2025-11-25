package com.diarioclasse.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.Criterio;

@Repository
public interface CriterioRepository extends JpaRepository<Criterio, Long> {
    
    // Método crucial: buscar todos os critérios que pertencem a uma UC específica
    List<Criterio> findByUnidadeCurricularId(Long ucId);

    // Método para buscar critérios por tipo de avaliação (Crítico/Desejável)
    List<Criterio> findByUnidadeCurricularIdAndTipoAvaliacao(Long ucId, String tipoAvaliacao);
}