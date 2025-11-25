package com.diarioclasse.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.diarioclasse.api.dto.AlunoDTO;
import com.diarioclasse.api.entities.Aluno;
import com.diarioclasse.api.servicies.AlunoService;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    // POST: Cadastrar novo Aluno
    @PostMapping
    public ResponseEntity<Aluno> cadastrarAluno(@RequestBody Aluno aluno) {
        Aluno novoAluno = alunoService.cadastrar(aluno);
        return new ResponseEntity<>(novoAluno, HttpStatus.CREATED);
    }

    // GET: Listar todos os Alunos
    @GetMapping
    public ResponseEntity<List<Aluno>> listarTodos() {
        List<Aluno> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }

    // GET: Buscar Aluno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(aluno);
    }
    
    // GET: Listar Alunos por Turma ID
    // Ex: /api/alunos/turma/1
 //   @GetMapping("/turma/{turmaId}")
 //   public ResponseEntity<List<Aluno>> listarPorTurma(@PathVariable Long turmaId) {
 //       List<Aluno> alunos = alunoService.listarPorTurma(turmaId);
 //       return ResponseEntity.ok(alunos);
 //   }

    // PUT: Atualizar Aluno
    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizarAluno(@PathVariable Long id, @RequestBody Aluno aluno) {
        Aluno alunoAtualizado = alunoService.atualizar(id, aluno);
        return ResponseEntity.ok(alunoAtualizado);
    }

    // DELETE: Deletar Aluno
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarAluno(@PathVariable Long id) {
        alunoService.deletar(id);
    }
    
 // Alterar o retorno do endpoint para List<AlunoDTO>
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<AlunoDTO>> listarPorTurma(@PathVariable Long turmaId) {
        // Chama o novo método do service que retorna DTOs
        List<AlunoDTO> alunos = alunoService.listarDTOsPorTurma(turmaId);
        return ResponseEntity.ok(alunos);
    }
}