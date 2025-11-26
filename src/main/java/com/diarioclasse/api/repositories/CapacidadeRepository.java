package com.diarioclasse.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.Capacidade;

@Repository
public interface CapacidadeRepository extends JpaRepository<Capacidade, Long> {
    List<Capacidade> findByUnidadeCurricularId(Long ucId);
}
