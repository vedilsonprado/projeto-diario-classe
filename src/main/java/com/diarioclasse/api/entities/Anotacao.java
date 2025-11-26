package com.diarioclasse.api.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_anotacao")
public class Anotacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT") // Permite textos longos
	private String texto;

	private LocalDateTime dataHora;

	// Campos para o Arquivo
	private String nomeArquivo;
	private String tipoArquivo; // Ex: image/png, application/pdf

	@Lob // Indica que é um objeto grande (BLOB)
	@Column(length = 10000000) // Aumenta o tamanho permitido para o arquivo
	private byte[] dadosArquivo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aluno_id", nullable = false)
	@JsonIgnoreProperties({ "turma", "lancamentosNotas", "anotacoes" })
	private Aluno aluno;

	// Construtor padrão
	public Anotacao() {
		this.dataHora = LocalDateTime.now(); // Pega a hora exata da criação
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public byte[] getDadosArquivo() {
		return dadosArquivo;
	}

	public void setDadosArquivo(byte[] dadosArquivo) {
		this.dadosArquivo = dadosArquivo;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}
}
