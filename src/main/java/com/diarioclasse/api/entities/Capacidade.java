package com.diarioclasse.api.entities;

import java.util.Set;

import com.diarioclasse.api.enums.TipoCapacidade;
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
@Table(name = "tb_capacidade")
public class Capacidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", nullable = false)
    private String descricao; // Ex: "Modelar Banco de Dados"

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_capacidade", nullable = false)
    private TipoCapacidade tipoCapacidade; // TÉCNICA ou SOCIOEMOCIONAL

    // Relação Pai: Unidade Curricular
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uc_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UnidadeCurricular unidadeCurricular;

    // Relação Filhos: Critérios
    // Removemos o JsonIgnore aqui para podermos ver os critérios ao listar a capacidade
    @OneToMany(mappedBy = "capacidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Criterio> criterios;

    public Capacidade() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public TipoCapacidade getTipoCapacidade() { return tipoCapacidade; }
    public void setTipoCapacidade(TipoCapacidade tipoCapacidade) { this.tipoCapacidade = tipoCapacidade; }
    public UnidadeCurricular getUnidadeCurricular() { return unidadeCurricular; }
    public void setUnidadeCurricular(UnidadeCurricular unidadeCurricular) { this.unidadeCurricular = unidadeCurricular; }
    public Set<Criterio> getCriterios() { return criterios; }
    public void setCriterios(Set<Criterio> criterios) { this.criterios = criterios; }
}
