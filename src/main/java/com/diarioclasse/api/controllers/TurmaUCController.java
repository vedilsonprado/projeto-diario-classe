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

import com.diarioclasse.api.entities.TurmaUC;
import com.diarioclasse.api.servicies.TurmaUCService;

@RestController
@RequestMapping("/api/associacoes/turmasuc")
@CrossOrigin(origins = "*")
public class TurmaUCController {

    @Autowired
    private TurmaUCService turmaUcService;

    // POST: Associar Turma a uma UC
    @PostMapping
    public ResponseEntity<TurmaUC> associarTurmaUC(@RequestBody TurmaUC associacao) {
        // Nota: O corpo da requisição deve conter os objetos Turma e UnidadeCurricular apenas com seus IDs.
        TurmaUC novaAssociacao = turmaUcService.associarTurmaUC(associacao);
        return new ResponseEntity<>(novaAssociacao, HttpStatus.CREATED);
    }

    // GET: Listar todas as UCs de uma Turma
    // Ex: /api/associacoes/turmasuc/turma/10
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<TurmaUC>> listarPorTurma(@PathVariable Long turmaId) {
        List<TurmaUC> lista = turmaUcService.listarPorTurma(turmaId);
        return ResponseEntity.ok(lista);
    }

    // DELETE: Desassociar Turma de uma UC (pelo ID da associação)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desassociarTurmaUC(@PathVariable Long id) {
        turmaUcService.desassociar(id);
    }
}
