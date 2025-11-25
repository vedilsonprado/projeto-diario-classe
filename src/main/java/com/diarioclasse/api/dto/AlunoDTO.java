package com.diarioclasse.api.dto;

import java.util.List;

public class AlunoDTO {
    private Long id;
    private String nomeCompleto;
    private String email;
    private List<DesempenhoUCDTO> desempenhos; // Nova lista
    

    public AlunoDTO() {}

    public AlunoDTO(Long id, String nomeCompleto, String email, List<DesempenhoUCDTO> desempenhos) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.desempenhos = desempenhos;
    }


	// Getters
    public Long getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public List<DesempenhoUCDTO> getDesempenhos() { return desempenhos; }
}