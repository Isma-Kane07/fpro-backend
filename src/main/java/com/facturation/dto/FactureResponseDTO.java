package com.facturation.dto;

import java.time.LocalDate;
import java.util.List;

public class FactureResponseDTO {

    private Long id;
    private String numeroFacture;
    private LocalDate dateFacturation;
    private String nomClient;
    private Double total;
    private String totalEnLettres; // Nouveau champ
    private List<LigneFactureDTO> lignes;

    // Constructeurs
    public FactureResponseDTO() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroFacture() { return numeroFacture; }
    public void setNumeroFacture(String numeroFacture) { this.numeroFacture = numeroFacture; }

    public LocalDate getDateFacturation() { return dateFacturation; }
    public void setDateFacturation(LocalDate dateFacturation) { this.dateFacturation = dateFacturation; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getTotalEnLettres() { return totalEnLettres; }
    public void setTotalEnLettres(String totalEnLettres) { this.totalEnLettres = totalEnLettres; }

    public List<LigneFactureDTO> getLignes() { return lignes; }
    public void setLignes(List<LigneFactureDTO> lignes) { this.lignes = lignes; }
}