package com.diarioclasse.api.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID único (chave primária, pode ser o número de matrícula)

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "email", unique = true)
    private String email;

    // Relacionamento N:1 (Muitos Alunos pertencem a Uma Turma)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false) // Coluna da Chave Estrangeira
    private Turma turma;
    
    // Relacionamento 1:N com LancamentoNota (Um Aluno tem Muitos Lançamentos de Notas)
    @JsonIgnore
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LancamentoNota> lancamentosNotas;

    // Construtor padrão (necessário para JPA)
    public Aluno() {
    }

    // Construtor para facilitar a criação (sem ID e coleções)
    public Aluno(String nomeCompleto, String email, Turma turma) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.turma = turma;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Set<LancamentoNota> getLancamentosNotas() {
        return lancamentosNotas;
    }

    public void setLancamentosNotas(Set<LancamentoNota> lancamentosNotas) {
        this.lancamentosNotas = lancamentosNotas;
    }
}
