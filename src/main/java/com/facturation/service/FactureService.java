package com.facturation.service;

import com.facturation.model.Facture;
import com.facturation.model.LigneFacture;
import com.facturation.dto.*;
import com.facturation.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private MontantEnLettresService montantEnLettresService;

    @Transactional
    public FactureResponseDTO creerFacture(FactureRequestDTO requestDTO) {
        // Créer la facture
        Facture facture = new Facture();
        facture.setNomClient(requestDTO.getNomClient());
        facture.setNumeroFacture(sequenceService.genererNumeroFacture());

        // Ajouter les lignes
        if (requestDTO.getLignes() != null) {
            for (LigneFactureDTO ligneDTO : requestDTO.getLignes()) {
                LigneFacture ligne = new LigneFacture();
                ligne.setQuantite(ligneDTO.getQuantite());
                ligne.setDesignation(ligneDTO.getDesignation());
                ligne.setPrixUnitaire(ligneDTO.getPrixUnitaire());
                facture.addLigne(ligne);
            }
        }

        // Calculer le total
        facture.calculerTotal();

        // Sauvegarder
        facture = factureRepository.save(facture);

        return convertirEnDTO(facture);
    }

    @Transactional(readOnly = true)
    public List<FactureResponseDTO> getAllFactures() {
        return factureRepository.findAll().stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<FactureResponseDTO> getAllFactures(Pageable pageable) {
        return factureRepository.findAll(pageable)
                .map(this::convertirEnDTO);
    }

    @Transactional(readOnly = true)
    public FactureResponseDTO getFactureById(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));
        return convertirEnDTO(facture);
    }

    @Transactional
    public FactureResponseDTO updateFacture(Long id, FactureRequestDTO requestDTO) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));

        // Mettre à jour les champs
        facture.setNomClient(requestDTO.getNomClient());

        // Supprimer les anciennes lignes
        facture.getLignes().clear();

        // Ajouter les nouvelles lignes
        if (requestDTO.getLignes() != null) {
            for (LigneFactureDTO ligneDTO : requestDTO.getLignes()) {
                LigneFacture ligne = new LigneFacture();
                ligne.setQuantite(ligneDTO.getQuantite());
                ligne.setDesignation(ligneDTO.getDesignation());
                ligne.setPrixUnitaire(ligneDTO.getPrixUnitaire());
                facture.addLigne(ligne);
            }
        }

        // Recalculer le total
        facture.calculerTotal();

        // Sauvegarder
        facture = factureRepository.save(facture);

        return convertirEnDTO(facture);
    }

    @Transactional
    public void deleteFacture(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID: " + id));

        factureRepository.delete(facture);
    }

    @Transactional(readOnly = true)
    public Page<FactureResponseDTO> searchFactures(String searchTerm, Pageable pageable) {
        return factureRepository.findByNomClientContainingIgnoreCaseOrNumeroFactureContainingIgnoreCase(
                        searchTerm, searchTerm, pageable)
                .map(this::convertirEnDTO);
    }

    private FactureResponseDTO convertirEnDTO(Facture facture) {
        FactureResponseDTO dto = new FactureResponseDTO();
        dto.setId(facture.getId());
        dto.setNumeroFacture(facture.getNumeroFacture());
        dto.setDateFacturation(facture.getDateFacturation());
        dto.setNomClient(facture.getNomClient());
        dto.setTotal(facture.getTotal());

        // Convertir le total en lettres seulement
        if (facture.getTotal() != null) {
            String totalEnLettres = montantEnLettresService.convertir(facture.getTotal());
            dto.setTotalEnLettres(totalEnLettres);
        }

        // Convertir les lignes
        List<LigneFactureDTO> lignesDTO = facture.getLignes().stream()
                .map(ligne -> {
                    LigneFactureDTO ligneDTO = new LigneFactureDTO();
                    ligneDTO.setId(ligne.getId());
                    ligneDTO.setQuantite(ligne.getQuantite());
                    ligneDTO.setDesignation(ligne.getDesignation());
                    ligneDTO.setPrixUnitaire(ligne.getPrixUnitaire());
                    return ligneDTO;
                })
                .collect(Collectors.toList());

        dto.setLignes(lignesDTO);
        return dto;
    }
}