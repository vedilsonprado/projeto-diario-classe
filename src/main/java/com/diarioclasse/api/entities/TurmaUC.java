package com.diarioclasse.api.entities;

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
@Table(name = "tb_turma_uc")
public class TurmaUC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento N:1 com Turma (Muitas entradas TurmaUC para Uma Turma)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    // Relacionamento N:1 com UnidadeCurricular (Muitas entradas TurmaUC para Uma UC)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uc_id", nullable = false)
    private UnidadeCurricular unidadeCurricular;

    // Coluna para atributos adicionais, se necessário (Ex: professor, data_inicio)
    @Column(name = "professor_responsavel")
    private String professorResponsavel; // Ou uma chave estrangeira para uma entidade Professor

    // Construtor padrão (necessário para JPA)
    public TurmaUC() {
    }

    // Construtor para facilitar a criação
    public TurmaUC(Turma turma, UnidadeCurricular unidadeCurricular, String professorResponsavel) {
        this.turma = turma;
        this.unidadeCurricular = unidadeCurricular;
        this.professorResponsavel = professorResponsavel;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public UnidadeCurricular getUnidadeCurricular() {
        return unidadeCurricular;
    }

    public void setUnidadeCurricular(UnidadeCurricular unidadeCurricular) {
        this.unidadeCurricular = unidadeCurricular;
    }

    public String getProfessorResponsavel() {
        return professorResponsavel;
    }

    public void setProfessorResponsavel(String professorResponsavel) {
        this.professorResponsavel = professorResponsavel;
    }
}
