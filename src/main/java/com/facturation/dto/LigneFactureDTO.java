package com.facturation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LigneFactureDTO {

    private Long id; // Ajout de l'ID pour les updates

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    @NotBlank(message = "La désignation est obligatoire")
    private String designation;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @Min(value = 0, message = "Le prix unitaire ne peut pas être négatif")
    private Double prixUnitaire;

    // Constructeurs
    public LigneFactureDTO() {}

    public LigneFactureDTO(Integer quantite, String designation, Double prixUnitaire) {
        this.quantite = quantite;
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    // Méthode pour calculer le montant
    public Double getMontant() {
        if (quantite != null && prixUnitaire != null) {
            return quantite * prixUnitaire;
        }
        return 0.0;
    }
}