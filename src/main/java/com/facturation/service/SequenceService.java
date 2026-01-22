package com.facturation.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SequenceService {

    private AtomicInteger compteur = new AtomicInteger(1);

    public String genererNumeroFacture() {
        LocalDate now = LocalDate.now();
        int annee = now.getYear();
        int mois = now.getMonthValue();
        int jour = now.getDayOfMonth();

        int sequence = compteur.getAndIncrement();
        return String.format("%02d%02d%02d-%03d", annee, mois, jour, sequence);
    }
}