package com.diarioclasse.api.entities;

import java.util.Set;

import com.diarioclasse.api.enums.TipoAvaliacao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_criterio")
public class Criterio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "descricao", nullable = false)
	private String descricao; // Ex: "Cria tabelas corretamente"

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_avaliacao", nullable = false)
	private TipoAvaliacao tipoAvaliacao; // CRÍTICO ou DESEJÁVEL

	// --- MUDANÇA: Agora aponta para Capacidade, não mais UC ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "capacidade_id", nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Capacidade capacidade;

	@JsonIgnore
	@OneToMany(mappedBy = "criterio", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<LancamentoNota> lancamentosNotas;

	public Criterio() {
	}

	// Getters e Setters Atualizados
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoAvaliacao getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(TipoAvaliacao tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}

	public Capacidade getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Capacidade capacidade) {
		this.capacidade = capacidade;
	}

	public Set<LancamentoNota> getLancamentosNotas() {
		return lancamentosNotas;
	}

	public void setLancamentosNotas(Set<LancamentoNota> lancamentosNotas) {
		this.lancamentosNotas = lancamentosNotas;
	}
}
