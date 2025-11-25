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

import com.diarioclasse.api.entities.UnidadeCurricular;
import com.diarioclasse.api.servicies.UnidadeCurricularService;

@RestController
@RequestMapping("/api/unidadescurriculares")
@CrossOrigin(origins = "*")
public class UnidadeCurricularController {

    @Autowired
    private UnidadeCurricularService ucService;

    // POST: Cadastrar nova UC
    @PostMapping
    public ResponseEntity<UnidadeCurricular> cadastrarUC(@RequestBody UnidadeCurricular uc) {
        UnidadeCurricular novaUC = ucService.cadastrar(uc);
        return new ResponseEntity<>(novaUC, HttpStatus.CREATED);
    }

    // GET: Listar todas as UCs
    @GetMapping
    public ResponseEntity<List<UnidadeCurricular>> listarTodas() {
        List<UnidadeCurricular> ucs = ucService.listarTodos();
        return ResponseEntity.ok(ucs);
    }

    // GET: Buscar UC por ID
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeCurricular> buscarPorId(@PathVariable Long id) {
        UnidadeCurricular uc = ucService.buscarPorId(id);
        return ResponseEntity.ok(uc);
    }

    // PUT: Atualizar UC
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeCurricular> atualizarUC(@PathVariable Long id, @RequestBody UnidadeCurricular uc) {
        UnidadeCurricular ucAtualizada = ucService.atualizar(id, uc);
        return ResponseEntity.ok(ucAtualizada);
    }

    // DELETE: Deletar UC
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUC(@PathVariable Long id) {
        ucService.deletar(id);
    }
}
