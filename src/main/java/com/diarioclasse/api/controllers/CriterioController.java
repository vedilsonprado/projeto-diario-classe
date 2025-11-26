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

    @PostMapping
    public ResponseEntity<Criterio> cadastrar(@RequestBody Criterio criterio) {
        Criterio novo = criterioService.cadastrar(criterio);
        return new ResponseEntity<>(novo, HttpStatus.CREATED);
    }

    // Endpoint necessário para o Modal de Edição de UC (Lista tudo de uma vez)
    @GetMapping("/unidade/{ucId}")
    public ResponseEntity<List<Criterio>> listarPorUnidadeCurricular(@PathVariable Long ucId) {
        // Agora este método existe no Service!
        List<Criterio> criterios = criterioService.listarPorUnidadeCurricular(ucId);
        return ResponseEntity.ok(criterios);
    }
    
    // Endpoint novo (Opcional, se quiser listar por capacidade especifica)
    @GetMapping("/capacidade/{capId}")
    public ResponseEntity<List<Criterio>> listarPorCapacidade(@PathVariable Long capId) {
        List<Criterio> criterios = criterioService.listarPorCapacidade(capId);
        return ResponseEntity.ok(criterios);
    }

    // ... (Demais métodos: listarTodos, buscarPorId, atualizar, deletar) ...
    // Mantenha o código padrão para esses
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        criterioService.deletar(id);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Criterio> atualizar(@PathVariable Long id, @RequestBody Criterio criterio) {
        return ResponseEntity.ok(criterioService.atualizar(id, criterio));
    }
}