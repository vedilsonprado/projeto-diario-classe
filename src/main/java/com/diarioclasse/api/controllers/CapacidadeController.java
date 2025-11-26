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

import com.diarioclasse.api.entities.Capacidade;
import com.diarioclasse.api.servicies.CapacidadeService;

@RestController
@RequestMapping("/api/capacidades")
@CrossOrigin(origins = "*") // Permite acesso do Frontend
public class CapacidadeController {

	@Autowired
	private CapacidadeService capacidadeService;

	// POST: Cadastrar nova Capacidade
	@PostMapping
	public ResponseEntity<Capacidade> cadastrar(@RequestBody Capacidade capacidade) {
		Capacidade novaCapacidade = capacidadeService.cadastrar(capacidade);
		return new ResponseEntity<>(novaCapacidade, HttpStatus.CREATED);
	}

	// GET: Listar Capacidades por UC
	// Ex: /api/capacidades/unidade/1
	@GetMapping("/unidade/{ucId}")
	public ResponseEntity<List<Capacidade>> listarPorUnidadeCurricular(@PathVariable Long ucId) {
		List<Capacidade> capacidades = capacidadeService.listarPorUnidadeCurricular(ucId);
		return ResponseEntity.ok(capacidades);
	}

	// GET: Buscar por ID
	@GetMapping("/{id}")
	public ResponseEntity<Capacidade> buscarPorId(@PathVariable Long id) {
		Capacidade capacidade = capacidadeService.buscarPorId(id);
		return ResponseEntity.ok(capacidade);
	}

	// PUT: Atualizar
	@PutMapping("/{id}")
	public ResponseEntity<Capacidade> atualizar(@PathVariable Long id, @RequestBody Capacidade capacidade) {
		Capacidade capacidadeAtualizada = capacidadeService.atualizar(id, capacidade);
		return ResponseEntity.ok(capacidadeAtualizada);
	}

	// DELETE: Excluir
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		capacidadeService.deletar(id);
	}
}