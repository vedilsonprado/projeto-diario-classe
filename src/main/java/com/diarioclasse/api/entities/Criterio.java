package com.diarioclasse.api.entities;

import java.util.Set;

import com.diarioclasse.api.enums.TipoAvaliacao;
import com.diarioclasse.api.enums.TipoCapacidade;
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
    private String descricao;

    // --- CAMPOS PESO E NOTA MAXIMA REMOVIDOS ---

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_avaliacao", nullable = false)
    private TipoAvaliacao tipoAvaliacao; // CRITICO ou DESEJAVEL

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_capacidade", nullable = false)
    private TipoCapacidade tipoCapacidade; // TECNICA ou SOCIOEMOCIONAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uc_id", nullable = false)
    //@JsonIgnore // Evitar loop
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UnidadeCurricular unidadeCurricular;

    @JsonIgnore // Evitar loop
    @OneToMany(mappedBy = "criterio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LancamentoNota> lancamentosNotas;

    // --- Construtores, Getters e Setters (Gere novamente no IDE para remover os antigos) ---
    public Criterio() {}

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

	public TipoCapacidade getTipoCapacidade() {
		return tipoCapacidade;
	}

	public void setTipoCapacidade(TipoCapacidade tipoCapacidade) {
		this.tipoCapacidade = tipoCapacidade;
	}

	public UnidadeCurricular getUnidadeCurricular() {
		return unidadeCurricular;
	}

	public void setUnidadeCurricular(UnidadeCurricular unidadeCurricular) {
		this.unidadeCurricular = unidadeCurricular;
	}

	public Set<LancamentoNota> getLancamentosNotas() {
		return lancamentosNotas;
	}

	public void setLancamentosNotas(Set<LancamentoNota> lancamentosNotas) {
		this.lancamentosNotas = lancamentosNotas;
	}
    
    
    
}
