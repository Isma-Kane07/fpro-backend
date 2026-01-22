package com.facturation.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lignes_facture")
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "designation", nullable = false, length = 500)
    private String designation;

    @Column(name = "prix_unitaire", nullable = false)
    private Double prixUnitaire;

    @Column(name = "montant", nullable = false)
    private Double montant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id", nullable = false)
    @JsonIgnore
    private Facture facture;

    // Constructeurs
    public LigneFacture() {}

    public LigneFacture(Integer quantite, String designation, Double prixUnitaire) {
        this.quantite = quantite;
        this.designation = designation;
        this.prixUnitaire = prixUnitaire;
        this.montant = quantite * prixUnitaire;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
        calculerMontant();
    }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerMontant();
    }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }

    // Méthode privée pour calculer le montant
    private void calculerMontant() {
        if (quantite != null && prixUnitaire != null) {
            this.montant = quantite * prixUnitaire;
        }
    }
}