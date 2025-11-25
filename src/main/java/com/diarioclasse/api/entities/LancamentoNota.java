package com.diarioclasse.api.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_lancamento_nota")
public class LancamentoNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "atingiu", nullable = false)
    private Boolean atingiu; // TRUE = Atingiu, FALSE = Não Atingiu

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterio_id", nullable = false)
    private Criterio criterio;

    public LancamentoNota() { this.dataLancamento = LocalDate.now(); }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getAtingiu() {
		return atingiu;
	}

	public void setAtingiu(Boolean atingiu) {
		this.atingiu = atingiu;
	}

	public LocalDate getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(LocalDate dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Criterio getCriterio() {
		return criterio;
	}

	public void setCriterio(Criterio criterio) {
		this.criterio = criterio;
	}

}
