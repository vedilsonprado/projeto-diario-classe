package com.diarioclasse.api.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_turma")
public class Turma {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_turma", nullable = false)
    private String nomeTurma;

    @Column(name = "ano_semestre", nullable = false)
    private String anoSemestre;

    // Relacionamento 1:N com Aluno (Uma Turma tem Muitos Alunos)
    // O atributo "turma" é o campo na classe Aluno que faz o mapeamento.
    @JsonIgnore
    @OneToMany(mappedBy = "turma", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Aluno> alunos;
    
    // Relacionamento N:N com UnidadeCurricular (via TurmaUC)
    // Usamos a entidade de junção TurmaUC para gerenciar esta relação
    @JsonIgnore
    @OneToMany(mappedBy = "turma", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TurmaUC> turmasUcs;

    // Construtor padrão (necessário para JPA)
    public Turma() {
    }

    // Construtor com campos
    public Turma(String nomeTurma, String anoSemestre) {
        this.nomeTurma = nomeTurma;
        this.anoSemestre = anoSemestre;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
    }

    public String getAnoSemestre() {
        return anoSemestre;
    }

    public void setAnoSemestre(String anoSemestre) {
        this.anoSemestre = anoSemestre;
    }

    public Set<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<Aluno> alunos) {
        this.alunos = alunos;
    }

    public Set<TurmaUC> getTurmasUcs() {
        return turmasUcs;
    }

    public void setTurmasUcs(Set<TurmaUC> turmasUcs) {
        this.turmasUcs = turmasUcs;
    }    
    
}