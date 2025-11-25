package com.diarioclasse.api.servicies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.entities.UnidadeCurricular;
import com.diarioclasse.api.repositories.UnidadeCurricularRepository;

@Service
public class UnidadeCurricularService {

    @Autowired
    private UnidadeCurricularRepository ucRepository;

    /**
     * Cadastra uma nova Unidade Curricular, verificando unicidade pelo nome.
     */
    public UnidadeCurricular cadastrar(UnidadeCurricular uc) {
        if (ucRepository.findByNome(uc.getNome()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Já existe uma Unidade Curricular com este nome: " + uc.getNome()
            );
        }
        return ucRepository.save(uc);
    }

    /**
     * Lista todas as Unidades Curriculares.
     */
    public List<UnidadeCurricular> listarTodos() {
        return ucRepository.findAll();
    }

    /**
     * Busca uma Unidade Curricular pelo ID.
     */
    public UnidadeCurricular buscarPorId(Long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Unidade Curricular não encontrada para o ID: " + id
            ));
    }

    /**
     * Atualiza os dados de uma Unidade Curricular existente.
     */
    public UnidadeCurricular atualizar(Long id, UnidadeCurricular ucDetalhes) {
        UnidadeCurricular ucExistente = buscarPorId(id);
        
        // Atualiza campos
        ucExistente.setNome(ucDetalhes.getNome());
        ucExistente.setCargaHoraria(ucDetalhes.getCargaHoraria());
        
        return ucRepository.save(ucExistente);
    }

    /**
     * Deleta uma Unidade Curricular pelo ID.
     */
    public void deletar(Long id) {
        UnidadeCurricular ucExistente = buscarPorId(id);
        ucRepository.delete(ucExistente);
    }
}
