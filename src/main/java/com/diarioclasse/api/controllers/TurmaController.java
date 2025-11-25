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

import com.diarioclasse.api.entities.Turma;
import com.diarioclasse.api.servicies.TurmaService;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    // POST: Cadastrar nova Turma
    @PostMapping
    public ResponseEntity<Turma> cadastrarTurma(@RequestBody Turma turma) {
        Turma novaTurma = turmaService.cadastrar(turma);
        return new ResponseEntity<>(novaTurma, HttpStatus.CREATED);
    }

    // GET: Listar todas as Turmas
    @GetMapping
    public ResponseEntity<List<Turma>> listarTodas() {
        List<Turma> turmas = turmaService.listarTodos();
        return ResponseEntity.ok(turmas);
    }

    // GET: Buscar Turma por ID
    @GetMapping("/{id}")
    public ResponseEntity<Turma> buscarPorId(@PathVariable Long id) {
        Turma turma = turmaService.buscarPorId(id);
        return ResponseEntity.ok(turma);
    }

    // PUT: Atualizar Turma
    @PutMapping("/{id}")
    public ResponseEntity<Turma> atualizarTurma(@PathVariable Long id, @RequestBody Turma turma) {
        Turma turmaAtualizada = turmaService.atualizar(id, turma);
        return ResponseEntity.ok(turmaAtualizada);
    }

    // DELETE: Deletar Turma
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 No Content para deleção bem-sucedida
    public void deletarTurma(@PathVariable Long id) {
        turmaService.deletar(id);
    }
}