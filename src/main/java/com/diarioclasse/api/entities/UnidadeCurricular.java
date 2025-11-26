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

    // --- MUDANÇA: Lista de Capacidades ---
    @OneToMany(mappedBy = "unidadeCurricular", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Capacidade> capacidades;

    @JsonIgnore
    @OneToMany(mappedBy = "unidadeCurricular", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TurmaUC> turmasUcs;

    public UnidadeCurricular() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }
    
    public Set<Capacidade> getCapacidade() { return capacidades; }
    public void setCapacidade(Set<Capacidade> capacidades) { this.capacidades = capacidades; }
    
    public Set<TurmaUC> getTurmasUcs() { return turmasUcs; }
    public void setTurmasUcs(Set<TurmaUC> turmasUcs) { this.turmasUcs = turmasUcs; }
}