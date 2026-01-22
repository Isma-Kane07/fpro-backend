package com.facturation.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", unique = true, nullable = false)
    private String numeroFacture;

    @Column(name = "date_facturation", nullable = false)
    private LocalDate dateFacturation;

    @Column(name = "nom_client", nullable = false)
    private String nomClient;

    @Column(name = "total", nullable = false)
    private Double total = 0.0;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneFacture> lignes = new ArrayList<>();

    // Constructeurs
    public Facture() {
        this.dateFacturation = LocalDate.now();
    }

    public Facture(String nomClient) {
        this();
        this.nomClient = nomClient;
    }

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

    public List<LigneFacture> getLignes() { return lignes; }
    public void setLignes(List<LigneFacture> lignes) { this.lignes = lignes; }

    // Méthodes utilitaires
    public void addLigne(LigneFacture ligne) {
        lignes.add(ligne);
        ligne.setFacture(this);
        calculerTotal();
    }

    public void calculerTotal() {
        this.total = lignes.stream()
                .mapToDouble(LigneFacture::getMontant)
                .sum();
    }
}