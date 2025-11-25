package com.diarioclasse.api.servicies;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.diarioclasse.api.dto.AlunoDTO;
import com.diarioclasse.api.dto.DesempenhoUCDTO;
import com.diarioclasse.api.entities.Aluno;
import com.diarioclasse.api.entities.Turma;
import com.diarioclasse.api.entities.TurmaUC;
import com.diarioclasse.api.entities.UnidadeCurricular;
import com.diarioclasse.api.enums.TipoAvaliacao;
import com.diarioclasse.api.repositories.AlunoRepository;
import com.diarioclasse.api.repositories.TurmaUCRepository;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private TurmaUCRepository turmaUcRepository;
    
    @Autowired
    private TurmaService turmaService; // Para validar se a turma existe

    /**
     * Cadastra um novo Aluno.
     */
    public Aluno cadastrar(Aluno aluno) {
        // 1. Verifica se a Turma existe
        Long turmaId = aluno.getTurma().getId();
        Turma turmaExistente = turmaService.buscarPorId(turmaId);
        
        // Atribui a Turma gerenciada pelo JPA ao Aluno
        aluno.setTurma(turmaExistente);

        // 2. Verifica se o email já está em uso (regra de unicidade)
        if (aluno.getEmail() != null && alunoRepository.findByEmail(aluno.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Já existe um aluno cadastrado com este e-mail: " + aluno.getEmail()
            );
        }

        return alunoRepository.save(aluno);
    }

    /**
     * Lista todos os Alunos.
     */
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }
    
    /**
     * Lista os Alunos pertencentes a uma Turma específica (chave para a exibição no frontend).
     */
    public List<Aluno> listarPorTurma(Long turmaId) {
        return alunoRepository.findByTurmaId(turmaId);
    }

    /**
     * Busca um Aluno pelo ID.
     */
    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, 
                "Aluno não encontrado para o ID: " + id
            ));
    }

    /**
     * Atualiza os dados de um Aluno existente.
     */
    public Aluno atualizar(Long id, Aluno alunoDetalhes) {
        Aluno alunoExistente = buscarPorId(id); 
        
        // Atualiza campos
        alunoExistente.setNomeCompleto(alunoDetalhes.getNomeCompleto());
        alunoExistente.setEmail(alunoDetalhes.getEmail());
        
        // Atualiza a Turma, se fornecida (precisa de validação adicional de Turma, omitida por brevidade)
        if (alunoDetalhes.getTurma() != null && alunoDetalhes.getTurma().getId() != null) {
             Turma novaTurma = turmaService.buscarPorId(alunoDetalhes.getTurma().getId());
             alunoExistente.setTurma(novaTurma);
        }
        
        return alunoRepository.save(alunoExistente);
    }

    /**
     * Deleta um Aluno pelo ID.
     */
    public void deletar(Long id) {
        Aluno alunoExistente = buscarPorId(id);
        alunoRepository.delete(alunoExistente);
    }
    
    public List<AlunoDTO> listarDTOsPorTurma(Long turmaId) {
        // 1. Busca os alunos da turma
        List<Aluno> alunos = alunoRepository.findByTurmaId(turmaId);
        
        // 2. Busca quais UCs essa turma cursa (para garantir a ordem das colunas)
        List<TurmaUC> turmasUcs = turmaUcRepository.findByTurmaId(turmaId);

        return alunos.stream().map(aluno -> {
            
            List<DesempenhoUCDTO> listaDesempenho = new ArrayList<>();

            // 3. Para cada UC da turma, calcula o desempenho desse aluno específico
            for (TurmaUC tuc : turmasUcs) {
                UnidadeCurricular uc = tuc.getUnidadeCurricular();

                // Filtra as notas do aluno APENAS para esta UC
                long countCC = aluno.getLancamentosNotas().stream()
                    .filter(n -> n.getCriterio().getUnidadeCurricular().getId().equals(uc.getId())) // Filtra pela UC
                    .filter(n -> n.getCriterio().getTipoAvaliacao() == TipoAvaliacao.CRITICO)
                    .filter(n -> Boolean.TRUE.equals(n.getAtingiu()))
                    .count();

                long countCD = aluno.getLancamentosNotas().stream()
                    .filter(n -> n.getCriterio().getUnidadeCurricular().getId().equals(uc.getId())) // Filtra pela UC
                    .filter(n -> n.getCriterio().getTipoAvaliacao() == TipoAvaliacao.DESEJAVEL)
                    .filter(n -> Boolean.TRUE.equals(n.getAtingiu()))
                    .count();
                
                listaDesempenho.add(new DesempenhoUCDTO(uc.getNome(), countCC, countCD));
            }

            return new AlunoDTO(aluno.getId(), aluno.getNomeCompleto(), aluno.getEmail(), listaDesempenho);
        }).toList();
    }
}
