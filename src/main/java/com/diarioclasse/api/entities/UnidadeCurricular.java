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
@Table(name = "tb_unidade_curricular")
public class UnidadeCurricular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;

    // Relacionamento 1:N com Criterio (Uma UC tem Muitos Critérios)
    @JsonIgnore
    @OneToMany(mappedBy = "unidadeCurricular", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Criterio> criterios;

    // Relacionamento N:N com Turma (via TurmaUC)
    @JsonIgnore
    @OneToMany(mappedBy = "unidadeCurricular", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TurmaUC> turmasUcs;

    // Construtor padrão (necessário para JPA)
    public UnidadeCurricular() {
    }

    // Construtor para facilitar a criação
    public UnidadeCurricular(String nome, Integer cargaHoraria) {
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Set<Criterio> getCriterios() {
        return criterios;
    }

    public void setCriterios(Set<Criterio> criterios) {
        this.criterios = criterios;
    }

    public Set<TurmaUC> getTurmasUcs() {
        return turmasUcs;
    }

    public void setTurmasUcs(Set<TurmaUC> turmasUcs) {
        this.turmasUcs = turmasUcs;
    }
}