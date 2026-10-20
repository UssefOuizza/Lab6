package org.example.dto;

import java.math.BigDecimal;

public class RapportVenteDTO {
    private String nomCategorie;
    private BigDecimal chiffreAffaires;

    public RapportVenteDTO() {}

    public RapportVenteDTO(String nomCategorie, BigDecimal chiffreAffaires) {
        this.nomCategorie = nomCategorie;
        this.chiffreAffaires = chiffreAffaires != null ? chiffreAffaires : BigDecimal.ZERO;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    @Override
    public String toString() {
        return "RapportVenteDTO{" +
                "categorie='" + nomCategorie + '\'' +
                ", chiffreAffaires=" + chiffreAffaires +
                '}';
    }
}
