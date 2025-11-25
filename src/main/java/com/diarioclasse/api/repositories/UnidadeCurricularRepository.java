package com.diarioclasse.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.UnidadeCurricular;

@Repository
public interface UnidadeCurricularRepository extends JpaRepository<UnidadeCurricular, Long> {
    
    // Método para evitar duplicação pelo nome da UC
    Optional<UnidadeCurricular> findByNome(String nome);
}