package com.facturation.repository;

import com.facturation.model.Facture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    // Recherche par nom client ou numéro de facture
    Page<Facture> findByNomClientContainingIgnoreCaseOrNumeroFactureContainingIgnoreCase(
            String nomClient, String numeroFacture, Pageable pageable);

    // Recherche avancée avec JPQL
    @Query("SELECT f FROM Facture f WHERE " +
            "LOWER(f.nomClient) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(f.numeroFacture) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Facture> searchFactures(@Param("search") String search, Pageable pageable);

    // Compter le nombre de factures par mois
    @Query("SELECT MONTH(f.dateFacturation) as mois, COUNT(f) as nombre " +
            "FROM Facture f " +
            "WHERE YEAR(f.dateFacturation) = :annee " +
            "GROUP BY MONTH(f.dateFacturation)")
    List<Object[]> countByMonth(@Param("annee") int annee);
}