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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.diarioclasse.api.entities.LancamentoNota;
import com.diarioclasse.api.servicies.LancamentoNotaService;

@RestController
@RequestMapping("/api/lancamentonotas")
@CrossOrigin(origins = "*")
public class LancamentoNotaController {

    @Autowired
    private LancamentoNotaService lancamentoNotaService;

    // POST: Lançar ou Atualizar Nota
    @PostMapping
    public ResponseEntity<LancamentoNota> lancarOuAtualizarNota(@RequestBody LancamentoNota lancamento) {
        LancamentoNota nota = lancamentoNotaService.lancarOuAtualizarNota(lancamento);
        return new ResponseEntity<>(nota, HttpStatus.CREATED);
    }

    // GET: Listar todos os lançamentos de um Aluno
    // Ex: /api/lancamentonotas/aluno/10
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<LancamentoNota>> listarPorAluno(@PathVariable Long alunoId) {
        List<LancamentoNota> lancamentos = lancamentoNotaService.listarPorAluno(alunoId);
        return ResponseEntity.ok(lancamentos);
    }

    // DELETE: Deletar um lançamento
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarLancamento(@PathVariable Long id) {
        lancamentoNotaService.deletar(id);
    }
}