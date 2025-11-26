package com.diarioclasse.api.servicies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.Capacidade;
import com.diarioclasse.api.entities.UnidadeCurricular;
import com.diarioclasse.api.repositories.CapacidadeRepository;

@Service
public class CapacidadeService {

	@Autowired
	private CapacidadeRepository capacidadeRepository;

	@Autowired
	private UnidadeCurricularService ucService; // Para validar a UC Pai

	/**
	 * Cadastra uma nova Capacidade, vinculando-a à Unidade Curricular.
	 */
	public Capacidade cadastrar(Capacidade capacidade) {
		// Verifica se a UC informada existe
		Long ucId = capacidade.getUnidadeCurricular().getId();
		UnidadeCurricular ucExistente = ucService.buscarPorId(ucId);

		// Garante o vínculo correto com a entidade gerenciada
		capacidade.setUnidadeCurricular(ucExistente);

		return capacidadeRepository.save(capacidade);
	}

	/**
	 * Lista todas as Capacidades de uma Unidade Curricular específica.
	 */
	public List<Capacidade> listarPorUnidadeCurricular(Long ucId) {
		// Apenas valida se a UC existe antes de buscar
		ucService.buscarPorId(ucId);
		return capacidadeRepository.findByUnidadeCurricularId(ucId);
	}

	/**
	 * Busca uma Capacidade pelo ID.
	 */
	public Capacidade buscarPorId(Long id) {
		return capacidadeRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Capacidade não encontrada para o ID: " + id));
	}

	/**
	 * Atualiza os dados de uma Capacidade existente.
	 */
	public Capacidade atualizar(Long id, Capacidade capacidadeDetalhes) {
		Capacidade capacidadeExistente = buscarPorId(id);

		// Atualiza campos básicos
		capacidadeExistente.setDescricao(capacidadeDetalhes.getDescricao());
		capacidadeExistente.setTipoCapacidade(capacidadeDetalhes.getTipoCapacidade());

		// Se houver tentativa de mudar a UC Pai
		if (capacidadeDetalhes.getUnidadeCurricular() != null
				&& capacidadeDetalhes.getUnidadeCurricular().getId() != null) {
			UnidadeCurricular novaUc = ucService.buscarPorId(capacidadeDetalhes.getUnidadeCurricular().getId());
			capacidadeExistente.setUnidadeCurricular(novaUc);
		}

		return capacidadeRepository.save(capacidadeExistente);
	}

	/**
	 * Deleta uma Capacidade. IMPORTANTE: Devido ao CascadeType.ALL na entidade,
	 * isso apagará todos os Critérios filhos.
	 */
	public void deletar(Long id) {
		Capacidade capacidadeExistente = buscarPorId(id);
		capacidadeRepository.delete(capacidadeExistente);
	}
}
