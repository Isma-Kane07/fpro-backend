package com.facturation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class FactureRequestDTO {

    @NotBlank(message = "Le nom du client est obligatoire")
    private String nomClient;

    @NotEmpty(message = "La facture doit contenir au moins une ligne")
    private List<LigneFactureDTO> lignes;

    // Constructeurs
    public FactureRequestDTO() {}

    public FactureRequestDTO(String nomClient, List<LigneFactureDTO> lignes) {
        this.nomClient = nomClient;
        this.lignes = lignes;
    }

    // Getters et Setters
    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public List<LigneFactureDTO> getLignes() { return lignes; }
    public void setLignes(List<LigneFactureDTO> lignes) { this.lignes = lignes; }
}