package com.diarioclasse.api.controllers;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.diarioclasse.api.entities.Anotacao;
import com.diarioclasse.api.servicies.AnotacaoService;

@RestController
@RequestMapping("/api/anotacoes")
@CrossOrigin(origins = "*")
public class AnotacaoController {

	@Autowired
	private AnotacaoService anotacaoService;

	/**
	 * POST: Criar Anotação Recebe dados via multipart/form-data para suportar
	 * upload de arquivos.
	 */
	@PostMapping(consumes = { "multipart/form-data" })
	public ResponseEntity<?> criarAnotacao(@RequestParam Long alunoId, @RequestParam String texto,
			@RequestParam MultipartFile arquivo) {
		try {
			Anotacao novaAnotacao = anotacaoService.criarAnotacao(alunoId, texto, arquivo);
			return new ResponseEntity<>(novaAnotacao, HttpStatus.CREATED);
		} catch (IOException e) {
			return new ResponseEntity<>("Erro ao processar o arquivo: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (RuntimeException e) {
			// Caso o aluno não seja encontrado (lançado pelo Service)
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * GET: Listar todas as anotações de um aluno específico
	 */
	@GetMapping("/aluno/{alunoId}")
	public ResponseEntity<List<Anotacao>> listarPorAluno(@PathVariable Long alunoId) {
		List<Anotacao> lista = anotacaoService.listarPorAluno(alunoId);
		return ResponseEntity.ok(lista);
	}

	/**
	 * DELETE: Excluir uma anotação pelo ID
	 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		anotacaoService.deletar(id);
	}
	
	// PUT: Atualizar Anotação
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> atualizarAnotacao(
            @PathVariable Long id,
            @RequestParam String texto,
            @RequestParam MultipartFile arquivo
    ) {
        try {
            Anotacao atualizada = anotacaoService.atualizarAnotacao(id, texto, arquivo);
            return new ResponseEntity<>(atualizada, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Erro ao processar arquivo.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}