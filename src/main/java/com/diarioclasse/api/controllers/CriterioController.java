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

import com.diarioclasse.api.entities.Criterio;
import com.diarioclasse.api.servicies.CriterioService;

@RestController
@RequestMapping("/api/criterios")
@CrossOrigin(origins = "*")
public class CriterioController {

    @Autowired
    private CriterioService criterioService;

    // POST: Cadastrar novo Critério
    @PostMapping
    public ResponseEntity<Criterio> cadastrarCriterio(@RequestBody Criterio criterio) {
        Criterio novoCriterio = criterioService.cadastrar(criterio);
        return new ResponseEntity<>(novoCriterio, HttpStatus.CREATED);
    }

    // GET: Listar todos os Critérios
    @GetMapping
    public ResponseEntity<List<Criterio>> listarTodos() {
        List<Criterio> criterios = criterioService.listarTodos();
        return ResponseEntity.ok(criterios);
    }

    // GET: Listar Critérios por ID da Unidade Curricular
    // Ex: /api/criterios/unidade/5
    @GetMapping("/unidade/{ucId}")
    public ResponseEntity<List<Criterio>> listarPorUnidadeCurricular(@PathVariable Long ucId) {
        List<Criterio> criterios = criterioService.listarPorUnidadeCurricular(ucId);
        return ResponseEntity.ok(criterios);
    }

    // GET: Buscar Critério por ID
    @GetMapping("/{id}")
    public ResponseEntity<Criterio> buscarPorId(@PathVariable Long id) {
        Criterio criterio = criterioService.buscarPorId(id);
        return ResponseEntity.ok(criterio);
    }

    // PUT: Atualizar Critério
    @PutMapping("/{id}")
    public ResponseEntity<Criterio> atualizarCriterio(@PathVariable Long id, @RequestBody Criterio criterio) {
        Criterio criterioAtualizado = criterioService.atualizar(id, criterio);
        return ResponseEntity.ok(criterioAtualizado);
    }

    // DELETE: Deletar Critério
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarCriterio(@PathVariable Long id) {
        criterioService.deletar(id);
    }
}