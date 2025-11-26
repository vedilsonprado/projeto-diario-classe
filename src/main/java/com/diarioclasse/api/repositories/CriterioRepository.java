package com.diarioclasse.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diarioclasse.api.entities.Criterio;

@Repository
public interface CriterioRepository extends JpaRepository<Criterio, Long> {

	// --- NOVO MÉTODO ---
	// O Spring vai buscar: Criterio -> campo "capacidade" -> campo "id"
	List<Criterio> findByCapacidadeId(Long capacidadeId);

	// --- OPCIONAL (Dica) ---
	// Se você precisar listar todos os critérios de uma UC inteira (ignorando a
	// capacidade específica),
	// você pode usar este nome de método (navegando pelo relacionamento):
	// Criterio -> Capacidade -> UnidadeCurricular -> Id
	List<Criterio> findByCapacidadeUnidadeCurricularId(Long ucId);
}