package com.diarioclasse.api.dto;

public class DesempenhoUCDTO {
    private String nomeUC;
    private long cc; // Críticos atingidos nesta UC
    private long cd; // Desejáveis atingidos nesta UC

    public DesempenhoUCDTO(String nomeUC, long cc, long cd) {
        this.nomeUC = nomeUC;
        this.cc = cc;
        this.cd = cd;
    }

    // Getters
    public String getNomeUC() { return nomeUC; }
    public long getCc() { return cc; }
    public long getCd() { return cd; }
}